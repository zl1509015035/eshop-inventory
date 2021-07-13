package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池：单例
 */
public class RequestProcessorThreadPool {

    /**
     * 实际项目中，设置线程池大小以及监控的内存队列大小，需要写到外部的配置文件
     *
     * 创建线程池 内存队列
     */
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();

    public RequestProcessorThreadPool(){
        for (int i = 0; i < 10; i++) {
            ArrayBlockingQueue<Request> queue = new ArrayBlockingQueue<Request>(100);
            queues.add(queue);

            threadPool.submit(new WorkerThread(queue));
        }
    }


    /**
     * 单例 饿汉 懒汉
     * 绝对安全：静态内部类的方式，去初始化单例
     */
    private static class Singleton{
        private static RequestProcessorThreadPool instance;

        static{
            instance = new RequestProcessorThreadPool();
        }

        public static RequestProcessorThreadPool getInstance(){
            return instance;
        }

    }

    /**
     * jvm机制去保证多线程并发安全
     *
     * 内部类的初始化，只会发生一次，不管多少个线程并发去初始化
     * @return
     */
    public static RequestProcessorThreadPool getInstance(){
        return Singleton.getInstance();
    }

    /**
     * 初始化的便捷方法
     */
    public static void init(){
        getInstance();
    }

}
