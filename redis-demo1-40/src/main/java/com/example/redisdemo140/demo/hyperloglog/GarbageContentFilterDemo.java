package com.example.redisdemo140.demo.hyperloglog;

import redis.clients.jedis.Jedis;

/**
 * 案例实战：网站重复垃圾数据的快速去重和过滤
 */
public class GarbageContentFilterDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        GarbageContentFilterDemo demo = new GarbageContentFilterDemo();
        demo.flushDB();

        String content = "正常内容";
        System.out.println("是否为垃圾内容:"+(demo.isGarbageContent(content)?"是":"否"));

        content = "垃圾内容";
        System.out.println("是否为垃圾内容:"+(demo.isGarbageContent(content)?"是":"否"));

        content = "垃圾内容";
        System.out.println("是否为垃圾内容:"+(demo.isGarbageContent(content)?"是":"否"));

    }

    public Boolean isGarbageContent(String content) {
        return jedis.pfadd("hyperloglog_content", content) == 0;
    }


}
