package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求异步处理的service实现，将请求放入相应的内存队列中
 */
@Service("requestAsyncProcessService")
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {

    @Override
    public void process(Request request) {

        try {

            //先做读请求的去重
            RequestQueue requestQueue = RequestQueue.getInstance();
            Map<Integer, Boolean> flagMap = requestQueue.getFlagMap();

            //如果是一个更新数据库的请求，那么将那个productId对应的标识设置为true
            if (request instanceof ProductInventoryDBUpdateRequest) {
                flagMap.put(request.getProductId(), true);

                //如果是一个读数据库的请求
            } else if (request instanceof ProductInventoryCacheRefreshRequest) {
                //查看flagMap中是否有此条商品id的信息
                Boolean flag = flagMap.get(request.getProductId());

                //如果flag是null
                if (flag == null) {
                    flagMap.put(request.getProductId(), false);
                }

                // 如果是缓存刷新的请求，那么就判断：1、如果标识不为空，而且是true，就说明之前有一个这个商品的数据库更新请求
                if (flag != null && flag) {
                    flagMap.put(request.getProductId(), false);
                }

                //如果是缓存刷新的请求，而且发现标识不给空，但是标识是false，说明前面已经有一个数据库更新请求+一个缓存刷新请求了
                if (flag != null && !flag) {
                    //对于这种重复的读请求，直接进行过滤就可以
                    return;
                }
            }
            //做请求的路由，根据每个请求的商品id，路由到相应的队列中去
            ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());
            //将请求放入对应的队列中，完成路由操作
            queue.put(request);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取路由到的内存队列
     *
     * @param productId
     * @return
     */
    public ArrayBlockingQueue<Request> getRoutingQueue(Integer productId) {
        //获取当前内存队列的大小
        RequestQueue requestQueue = RequestQueue.getInstance();

        //先获取productId的hash值
        String key = String.valueOf(productId);
        int h;
        int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        //对hash值进行取模，将hash值路由到指定的内存队中
        int index = (requestQueue.queueSize() - 1) & hash;

        System.out.println("=========日志==========:路由内存对垒，商品id=" + productId + ",队列索引=" + index);
        return requestQueue.getQueue(index);
    }
}
