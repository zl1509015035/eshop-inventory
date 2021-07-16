package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

/**
 * 实现一个简单的  唯一ID生成器
 * <p>
 * snowflake 算法
 * <p>
 * incr
 */
public class JedisTest44 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("jsseps_0");

        jedis.del("order_id_counter");
        //唯一ID生成器
        for (int i = 0; i < 10; i++) {
            Long orderId = jedis.incr("order_id_counter");
            System.out.println("生成的第"+(i+1)+"个唯一ID："+orderId);
        }
    }
}
