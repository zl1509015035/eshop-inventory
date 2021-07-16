package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

/**
 *  案例实战：社交网站的网址点击追踪机制
 *
 *  hash
 *  list
 *  set
 *  sorted set
 */
public class JedisTest46 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("jsseps_0");

        jedis.del("article:1:dianzan");

    }
}
