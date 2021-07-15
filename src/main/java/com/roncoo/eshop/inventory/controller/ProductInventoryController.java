package com.roncoo.eshop.inventory.controller;

import com.roncoo.eshop.inventory.model.ProductInventory;
import com.roncoo.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.service.ProductInventoryService;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import com.roncoo.eshop.inventory.vo.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 商品库存controller
 *
 *场景：
 * （1）一个更新商品库存的请求过来，然后此时会先删除redis中的缓存，然后模拟卡顿5秒钟
 * （2）在这个卡顿的5秒钟内，我们发送一个商品缓存的读请求，因为此时redis中没有缓存，就会来请求将数据库中最新数据刷新到缓存中
 * （3）此时读请求会路由到同一个内存队列中，阻塞住，不会执行
 * （4）等5秒后，写请求完成了数据库的更新之后，读请求才会执行
 * （5）读请求执行的时候，会将最新的库存从数据库中查询出来，然后更新到缓存中
 *
 * 如果是不一致的情况，可能出现在redis中库存还是100，但数据库中也许已经是99了
 *
 * 现在做了一致性保障方案后，就可以保障数据时一致的
 */
@RestController
public class ProductInventoryController {

    @Resource
    private RequestAsyncProcessService requestAsyncProcessService;

    @Resource
    private ProductInventoryService productInventoryService;

    /**
     * 更新商品库存
     */
    @RequestMapping("/updateProductInventory")
    public Response updateProductInventory(ProductInventory productInventory) {

        System.out.println("=========日志==========:接收到更新商品库存的请求，商品id="+productInventory.getProductId()
                + ",商品库存数量="+productInventory.getInventoryCnt());

        Response response = null;

        try {
            Request request = new ProductInventoryDBUpdateRequest(
                    productInventory, productInventoryService);
            requestAsyncProcessService.process(request);
            response = new Response(Response.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response((Response.FAILURE));
        }
        return response;
    }

    /**
     * 获取商品库存
     */
    @RequestMapping("/getProductInventory")
    public ProductInventory getProductInventory(Integer productId) {

        System.out.println("=========日志==========:接收一个商品库存的读请求，商品id="+productId);


        ProductInventory productInventory = null;

        try {
            Request request = new ProductInventoryCacheRefreshRequest(
                    productId, productInventoryService,false);
            //将请求异布处理，扔进内存队列相应的block队列中
            requestAsyncProcessService.process(request);

            //请求扔给service异步处理后，需要wile(true)一会
            //尝试等待前面有商品库存更新的操作，同时将缓存刷新至redis
            long startTime = System.currentTimeMillis();
            long waitTime = 0L;
            long endTime = 0L;

            while (true) {

                System.out.println("=========日志==========:waitTime="+waitTime);

                if (waitTime > 200) {
                    break;
                }
                //尝试去redis中读取一次redis缓存
                productInventory = productInventoryService.getProductInventoryCache(productId);

                //如果读取到结果,返回
                if (productInventory != null) {
                    System.out.println("=========日志==========:在200毫秒内读到了缓存,商品id=" + productInventory.getProductId()+",商品库存数量="+productInventory.getInventoryCnt());
                    return productInventory;
                }
                //如果没读取到结果,等待一段时间
                else {
                    Thread.sleep(20);
                    endTime = System.currentTimeMillis();
                    waitTime = endTime - startTime;
                }
            }
            //缓存中读取不到，直接尝试从数据库中读取数据
            productInventory = productInventoryService.findProductInventory(productId);
            if(productInventory != null){
                //将缓存刷新一下
                //这个过程中是一个读操作的过程，但是没有放在队列中串行去处理，还是有数据不一致的问题
//                productInventoryService.setProductInventoryCache(productInventory);
                request = new ProductInventoryCacheRefreshRequest(
                        productId, productInventoryService,true);

                requestAsyncProcessService.process(request);

                //代码运行至这里，会有三种情况：
                //1、上一次也是读请求，数据刷入redis，但LRU清理掉了，标志位还是false
                //所以此时下一个读请求是从缓存中拿不到数据的，再放一个读Request进队列，让数据去刷新一下
                //2、可能在200ms内，读请求在队列中一直积压，没等到它执行，所以就直接查一次库，然后给队列里塞进去一个刷新缓存的请求
                //3、数据库中本身就没有
                return productInventory;
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Response((Response.FAILURE));
        }
        return new ProductInventory(productId, -1L);
    }
}
