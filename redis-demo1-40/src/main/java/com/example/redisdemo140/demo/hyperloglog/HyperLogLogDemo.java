package com.example.redisdemo140.demo.hyperloglog;

import com.example.redisdemo140.demo.sortedset.RecommendProductDemo;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 基于HyperLogLog的网站UV统计程序
 *
 * hyperloglog 数据结构+概率算法，组合而成，去重统计，得到一个近似值
 *
 * 只占用12kb内存
 *
 * pfadd key 一大堆item，可以对数据进行计数，如果计算过这个元素，就返回0，没计算过就返回1
 *
 * pfcount key 可以获取计数结果
 */
public class HyperLogLogDemo {

    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        HyperLogLogDemo demo = new HyperLogLogDemo();
        demo.flushDB();
        demo.initUVData();
        //只能获取一个近似值，不是精确值
        long uv = demo.getUV();
        System.out.println("今日访问UV的用户数为:"+uv);
    }

    /**
     * 初始化UV值
     */
    public void initUVData(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        for (int i = 0; i < 1100; i++) {
            for (int j = 0; j < 10; j++) {
                jedis.pfadd("hyperloglog_uv_"+today,String.valueOf(i+1));
            }
        }
    }

    /**
     * 获取UV值
     * @return
     */
    public long getUV(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());
        return jedis.pfcount("hyperloglog_uv_"+today);
    }


}
