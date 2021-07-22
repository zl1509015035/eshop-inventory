package com.example.redisdemo140.demo.expire;

import redis.clients.jedis.Jedis;

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

        Thread.sleep(12*1000);

        Boolean result = demo.lock("test_lock", "testvalue", 10);
        System.out.println("第二次加锁结果:" + (result ? "是" : "否"));


    }

    public Boolean lock(String key, String value, int timeout) {
        Long result = jedis.setnx(key, value);
        jedis.expire(key, timeout);
        return result > 0;
    }

    public void unlock(String key) {
        jedis.del(key);
    }
}
