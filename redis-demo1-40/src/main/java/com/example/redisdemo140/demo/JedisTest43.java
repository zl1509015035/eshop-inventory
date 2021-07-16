package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

public class JedisTest43 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.1.10", 6379);
        jedis.auth("redis-pass");




    }
}
