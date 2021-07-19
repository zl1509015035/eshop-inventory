package com.example.redisdemo140.demo.list;

import redis.clients.jedis.Jedis;

/**
 * 案例实战：秒杀活动下的公平队列抢购机制
 * list
 * <p>
 * 将所有涌入系统得秒杀抢购请求，都放入redis的list数据结构中，进行公平队列排队，进队后等待秒杀结果
 * 专门做一个消费者从list里按照顺序获取抢购请求，按顺序进行库存扣减，扣减成功了就代表抢购成功
 * <p>
 * lpush list request
 * rpop list
 */
public class SecKillDemo {
    Jedis jedis = new Jedis("192.168.1.10", 6379);


    /**
     * 秒杀抢购请求入队
     *
     * @param secKillRequest
     */
    public void enqueueSecKillRequest(String secKillRequest) {
        jedis.lpush("sec_kill_request_queue", secKillRequest);
    }

    /**
     * 秒杀抢购请求出队
     *
     * @return
     */
    public String dequeueSecKillRequest() {
        return jedis.rpop("sec_kill_request_queue");
    }

    public static void main(String[] args) {
        SecKillDemo demo = new SecKillDemo();

        for (int i = 0; i < 10; i++) {
            demo.enqueueSecKillRequest("第" + (i + 1) + "个秒杀请求!");
        }

        while (true) {
            String secKillRequest = demo.dequeueSecKillRequest();
            if (secKillRequest == null || "null".equals(secKillRequest) || "".equals(secKillRequest)) {
                break;
            }
            System.out.println(secKillRequest);
        }
    }
}
