package com.roncoo.eshop.inventory.request;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 请求的内存队列
 */
public class RequestQueue {

    //    内存队列
    private List<ArrayBlockingQueue<Request>> queues = new ArrayList<ArrayBlockingQueue<Request>>();

    /**
     * 单例 饿汉 懒汉
     * 绝对安全：静态内部类的方式，去初始化单例
     */
    private static class Singleton{
        private static RequestQueue instance;

        static{
            instance = new RequestQueue();
        }

        public static RequestQueue getInstance(){
            return instance;
        }

    }

    /**
     * jvm机制去保证多线程并发安全
     *
     * 内部类的初始化，只会发生一次，不管多少个线程并发去初始化
     * @return
     */
    public static RequestQueue getInstance(){
        return RequestQueue.Singleton.getInstance();
    }

    public void addQueue(ArrayBlockingQueue<Request> queue){
        this.queues.add(queue);

    }


}
