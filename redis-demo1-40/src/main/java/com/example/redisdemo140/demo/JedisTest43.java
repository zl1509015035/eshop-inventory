package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

public class JedisTest43 {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.1.10", 6379);
        jedis.auth("redis-pass");

        jedis.del("operation_log_2021_01_01");

        for (int i = 0; i < 10; i++) {
            jedis.append("operation_log_2021_01_01","今天的第"+(i+1)+"条操作日志\n");
        }

        String operationLog = jedis.get("operation_log_2021_01_01");
        System.out.println("今天所有的操作日志:\n"+operationLog);

    }
}
