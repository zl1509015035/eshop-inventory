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
        ProductInventory productInventory = null;


        try {
            Request request = new ProductInventoryCacheRefreshRequest(
                    productId, productInventoryService);
            requestAsyncProcessService.process(request);

            //请求扔给service异步处理后，需要wile(true)一会
            //尝试等待前面有商品库存更新的操作，同时将缓存刷新至redis
            long startTime = System.currentTimeMillis();
            long waitTime = 0L;
            long endTime = 0L;

            while (true) {

                if (waitTime > 200) {
                    break;
                }
                //尝试去redis中读取一次redis缓存
                productInventory = productInventoryService.getProductInventoryCache(productId);

                //如果读取到结果,返回
                if (productInventory != null) {
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
                return productInventory;
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Response((Response.FAILURE));
        }
        return new ProductInventory(productId, -1L);
    }
}
