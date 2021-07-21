package com.example.redisdemo140.demo.sortedset;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 新闻浏览案例
 */
public class NewsDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        NewsDemo demo = new NewsDemo();
        for (int i = 0; i < 20; i++) {
            demo.addNews(i + 1, i + 1);
        }
        long minTimestamp = 2;
        long maxTimestamp = 18;

        int pageNo = 2;
        int pageSize = 10;

        int startIndex = (pageNo - 1) * 10;

        Set<Tuple> searchNews = demo.searchNews(maxTimestamp, minTimestamp, startIndex, pageSize);
        Iterator<Tuple> iterator = searchNews.iterator();
        System.out.println("搜索指定时间范围内的第一页");
        while(iterator.hasNext()){
            Tuple next = iterator.next();
            System.out.println(next.getElement()+" "+next.getScore());
        }
    }

    /**
     * 加入一篇新闻
     *
     * @param newsId
     */
    public void addNews(long newsId, long timestamp) {
        jedis.zadd("news", timestamp, String.valueOf(newsId));
    }

    /**
     * 搜索新闻
     *
     * @return
     */
    public Set<Tuple> searchNews(long maxTimestamp, long minTimestamp, int index, int count) {
        return jedis.zrevrangeByScoreWithScores("news", maxTimestamp, minTimestamp, index, count);
    }

}
