package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

import java.util.List;

public class JedisTest41 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.1.10", 6379);
        jedis.auth("redis-pass");

        jedis.del("article:1:title");
        jedis.del("article:1:content");
        jedis.del("article:1:author");
        jedis.del("article:1:time");

        //博客的发布、修改、查看
        Long publicBlogResult = jedis.msetnx("article:1:title", "学习Redis",
                "article:1:content", "如何学好Redis的使用",
                "article:1:author", "中华石衫",
                "article:1:time", "2020-01-01 00:00:00");
        System.out.println("发布博客的结果：" + publicBlogResult);

        List<String> blog = jedis.mget("article:1:title",
                "article:1:content",
                "article:1:author",
                "article:1:time");
        System.out.println("查看博客：" + blog);

        String updateBlogResult = jedis.mset("article:1:title", "修改后的学习Redis",
                "article:1:content", "修改后的如何学好Redis的使用");
        System.out.println("修改博客的结果:" + updateBlogResult);

        blog = jedis.mget("article:1:title",
                "article:1:content",
                "article:1:author",
                "article:1:time");
        System.out.println("再次查看博客：" + blog);

        //查看博客的长度统计
        Long blogLength = jedis.strlen("article:1:content");
        System.out.println("博客的长度统计=" + blogLength);

        //截取博客内容部分内容
        String blogContentPreview = jedis.getrange("article:1:content", 0, 5);
        System.out.println("博客内容预览:"+blogContentPreview);

    }
}
