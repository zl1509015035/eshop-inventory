package com.example.redisdemo140.demo.expire;

import redis.clients.jedis.Jedis;

/**
 * 案例实战:带有自动过期时间的分布式缓存实现
 */
public class ExpireDemo {
    private static Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) throws Exception {
        jedis.set("test_key", "test_value");
        jedis.expire("test_key", 10);
        System.out.println(jedis.get("test_key"));
        Thread.sleep(12 * 1000);
        String testValue = jedis.get("test_key");
        System.out.println("数据是否过期:"+((testValue == null || "null".equals(testValue))?"是":"否"));
    }


}
