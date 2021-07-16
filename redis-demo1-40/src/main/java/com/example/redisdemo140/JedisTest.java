package com.example.redisdemo140;


import redis.clients.jedis.Jedis;

public class JedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.1.10", 6379);
        jedis.auth("redis-pass");

        //最简单的缓存实例
        jedis.set("key1", "value1");
        System.out.println(jedis.get("key1"));

        //
        jedis.del("local_test");
        Long result1 = jedis.setnx("local_test", "value_test");
        System.out.println("第一次加锁的结果:" + result1);

        Long result2 = jedis.setnx("local_test", "value_test");
        System.out.println("第二次加锁的结果:" + result2);

        jedis.del("local_test");

        Long result3 = jedis.setnx("local_test", "value_test");
        System.out.println("第三次加锁的结果:" + result3);


    }
}
