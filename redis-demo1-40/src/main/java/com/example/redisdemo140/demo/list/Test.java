package com.example.redisdemo140.demo.list;

import redis.clients.jedis.Jedis;

/**
 * 案例实战：秒杀活动下的公平队列抢购机制
 * list
 *
 * 将所有涌入系统得秒杀抢购请求，都放入redis的list数据结构中，进行公平队列排队，进队后等待秒杀结果
 * 专门做一个消费者从list里按照顺序获取抢购请求，按顺序进行库存扣减，扣减成功了就代表抢购成功
 *
 * lpush list request
 * rpop list
 */
public class Test {

    public static void main(String[] args) {
         Jedis jedis = new Jedis("127.0.0.1", 6379);

        String ping = jedis.ping();
        System.out.println(ping);
    }
}
