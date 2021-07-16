package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

/**
 * 实现一个博客点赞计数器
 */
public class JedisTest45 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("jsseps_0");

        jedis.del("article:1:dianzan");
        //博客点赞计数器
        for(int i = 0 ;i<10;i++){
            jedis.incr("article:1:dianzan");
        }
        Long dianzanCounter = Long.valueOf(jedis.get("article:1:dianzan"));
        System.out.println("博客的点赞次数为："+dianzanCounter);

        //博客取消点赞
        for(int i = 0 ;i<10;i++){
            jedis.decr("article:1:dianzan");
        }
        Long dianzanCounter2 = Long.valueOf(jedis.get("article:1:dianzan"));
        System.out.println("博客的取消一次，当前点赞数为："+dianzanCounter2);

    }
}
