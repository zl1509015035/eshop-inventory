package com.example.redisdemo140.demo.expire;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.io.IOException;

/**
 * 案例实战：超时自动释放锁
 */
public class TimeoutDistributedLockDemo {
    private static Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) throws Exception {
        TimeoutDistributedLockDemo demo = new TimeoutDistributedLockDemo();
        demo.flushDB();
        demo.lock("test_lock", "testvalue", 10);

        Thread.sleep(12 * 1000);

        Boolean result = demo.lock("test_lock", "testvalue", 10);
        System.out.println("第二次加锁结果:" + (result ? "是" : "否"));

        System.out.println("不是你本人能否释放锁:"+(demo.unlock("test_lock", "xxx")?"能":"不能"));
        System.out.println("是你本人能否释放锁:"+(demo.unlock("test_lock", "testvalue")?"能":"不能"));


    }

    public Boolean lock(String key, String value, int timeout) {
        Long result = jedis.setnx(key, value);
        jedis.expire(key, timeout);
        return result > 0;
    }

    /**
     * 释放锁，需要判断是否是你自己加的锁，才可以释放锁
     *
     * @param key
     */
    public Boolean unlock(String key, String vlaue) {
        //在pipeline取出来
        String currentValue = jedis.get(key);

        Pipeline pipeline = jedis.pipelined();

        try {
            pipeline.watch(key);
            //为空则已经释放掉了
            if (currentValue == null || currentValue.equals("") || currentValue.equals("null")) {
                return true;
            }

            if (currentValue.equals(vlaue)) {
                pipeline.multi();
                pipeline.del(key);
                pipeline.exec();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                pipeline.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
