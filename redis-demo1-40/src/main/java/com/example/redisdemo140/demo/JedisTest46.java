package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

/**
 * 案例实战：社交网站的网址点击追踪机制
 * <p>
 * hash
 * list
 * set
 * sorted set
 */
public class JedisTest46 {

    private static final String X36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String[] X36_ARRAY = "0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z".split(",");

    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public JedisTest46(){
        jedis.set("short_url_seed","51167890045");
    }

    public String getShortUrl(String url) {
        Long shortUrlSeed = jedis.incr("short_url_seed");
        StringBuffer buffer = new StringBuffer();

        while (shortUrlSeed > 0) {
            buffer.append(X36_ARRAY[(int) (shortUrlSeed % 36)]);
            shortUrlSeed = shortUrlSeed / 36;
        }

        String shortUrl = buffer.reverse().toString();
        jedis.hset("short_url_access_count", shortUrl, "0");
        jedis.hset("short_url_mapping", shortUrl, url);
        return shortUrl;
    }

    /**
     * 给短连接的地址进行点击数量增长
     *
     * @param shortUrl
     */
    public void incrementShortUrlAccessCount(String shortUrl) {
        jedis.hincrBy("short_url_access_count", shortUrl, 1);
    }

    /**
     * 获取短连接地址的访问次数
     *
     * @param shortUrl
     */
    public long getShortUrlAccessCount(String shortUrl) {
        return Long.valueOf(jedis.hget("short_url_access_count", shortUrl));
    }

    public static void main(String[] args) {

        JedisTest46 demo = new JedisTest46();
        //获取短链接
        String shortUrl = demo.getShortUrl("http://redis.com/index.html");
        System.out.println("页面上展示的短链接地址为:" + shortUrl);

        for (int i = 0; i < 152; i++) {
            //对短链接进行访问
            demo.incrementShortUrlAccessCount(shortUrl);
        }

        long accessCount = demo.getShortUrlAccessCount(shortUrl);
        System.out.println("短链接被访问的次数为：" + accessCount);


    }
}
