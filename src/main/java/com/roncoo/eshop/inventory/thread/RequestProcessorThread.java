package com.roncoo.eshop.inventory.thread;

import com.roncoo.eshop.inventory.request.Request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

/**
 * 执行请求的工作线程
 */
public class RequestProcessorThread implements Callable<Boolean> {

    private ArrayBlockingQueue<Request> queue;

    public RequestProcessorThread(ArrayBlockingQueue<Request> queue){
        this.queue = queue;
    }

    @Override
    public Boolean call() throws Exception {
        while(true){
            break;
        }
        return true;
    }
}
