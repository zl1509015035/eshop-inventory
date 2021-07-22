package com.example.redisdemo140.demo.hyperloglog;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 案例实战:网站日常指标统计
 */
public class WebsiteStatisticsDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        WebsiteStatisticsDemo demo = new WebsiteStatisticsDemo();
        demo.flushDB();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        long duplicateUv = 0;

        for (int i = 0; i < 7; i++) {
            String date = dateFormat.format(calendar.getTime());
            demo.initUVData(date);

            long uv = demo.getUV(date);
            System.out.println("日期为:" + date + "的UV值为:" + uv);

            duplicateUv += uv;
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        long weeklyUV = demo.getWeeklyUV();
        System.out.println("实际的周活跃用户数:"+weeklyUV);
    }

    /**
     * 初始化某一天的UV数据
     */
    public void initUVData(String date) {
        Random random = new Random();
        int startIndex = random.nextInt(1000);
        System.out.println("今日访问uv起始id为:" + startIndex);


        for (int i = startIndex; i < startIndex + 1358; i++) {
            for (int j = 0; j < 10; j++) {
                jedis.pfadd("hyperloglog_uv_" + date, String.valueOf(i + 1));
            }
        }
    }

    /**
     * 获取UV数据
     *
     * @param date
     * @return
     */
    public long getUV(String date) {
        return jedis.pfcount("hyperloglog_uv_" + date);
    }

    public long getWeeklyUV() {
        List<String> keys = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            String date = dateFormat.format(calendar.getTime());
            keys.add("hyperloglog_uv_" + date);
        }

        String[] keyArray = keys.toArray(new String[keys.size()]);

        jedis.pfmerge("weekly_uv", keyArray);

        return jedis.pfcount("weekly_uv");
    }

}
