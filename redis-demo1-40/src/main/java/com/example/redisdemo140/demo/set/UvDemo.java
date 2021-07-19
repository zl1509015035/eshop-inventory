package com.example.redisdemo140.demo.set;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 案例：网站每日UV数据指标去重统计
 * <p>
 * 统计多少用户访问了网站，并对相同用户多次访问进行去重
 */
@Slf4j
public class UvDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public static void main(String[] args) {
        UvDemo demo = new UvDemo();
        for (int i = 0; i < 105; i++) {
            long userId = i+1;
            for (int j = 0; j < 10; j++) {
                demo.addUserAcess(userId);
            }
        }
        long uv = demo.getUV();
        System.out.println("当日uv为:"+uv);
    }

    /**
     * 添加一次用户的访问记录
     */
    public void addUserAcess(long userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        jedis.sadd("user_access::" + today, String.valueOf(userId));
    }

    /**
     * 获取当天网站UV的值
     *
     * @return
     */
    public long getUV() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        return jedis.scard("user_access::" + today);
    }
}
