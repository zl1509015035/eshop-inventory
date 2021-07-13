package com.roncoo.eshop.inventory.service.impl;

import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;
import com.roncoo.eshop.inventory.service.RequestAsyncProcessService;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求异步处理的service实现
 */
@Service("requestAsyncProcessService")
public class RequestAsyncProcessServiceImpl implements RequestAsyncProcessService {
    @Override
    public void process(Request request) {
        /**
         * 做请求的路由，根据每个请求的商品id，路由到对应的内存队里中去
         */
        ArrayBlockingQueue<Request> queue = getRoutingQueue(request.getProductId());

        //将请求放入对应队列中，完成路由操作
        queue.add(request);
    }

    /**
     * 获取路由到的内存队列
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
        return requestQueue.getQueue(index);
    }
}
