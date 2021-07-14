package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.ProductInventoryCacheRefreshRequest;
import com.roncoo.eshop.inventory.request.ProductInventoryDBUpdateRequest;
import com.roncoo.eshop.inventory.request.Request;
import com.roncoo.eshop.inventory.request.RequestQueue;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行请求的工作线程
 */
public class RequestProcessorThread implements Callable<Boolean> {

    private ArrayBlockingQueue<Request> queue;

    public RequestProcessorThread(ArrayBlockingQueue<Request> queue) {
        this.queue = queue;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            while (true) {
                //ArrayBlockingQueue
                //Blocking就是说明，如果队列满了，或者是空的，那么都会在执行操作的时候，阻塞住
                Request request = queue.take();

                System.out.println("=========日志==========:工作线程处理请求，商品id=" + request.getProductId());


                boolean forceRefresh = request.isForceRefresh();

                //先做读请求的去重
                if (!forceRefresh) {
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
                            return true;
                        }
                    }

                }


                //执行这个request操作
                request.process();

                //假如执行完一个读请求后，假设数据已经刷新到redis中
                //但是可能redis满了，从内存中自动清理了，又来一个读请求，此时标志位是false，就不会执行刷新缓存的操作
                //所以在执行完读请求之后，实际上这个标志位是停留在false
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }
}
