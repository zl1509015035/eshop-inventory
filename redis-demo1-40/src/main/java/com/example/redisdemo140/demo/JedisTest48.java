package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * 博客案例 --使用hash改写   博客网站3.0
 */
public class JedisTest48 {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public long getBlogId() {
        jedis.flushDB();
        Long orderIdCounter = jedis.incr("order_id_counter");
        return orderIdCounter;
    }

    public static void main(String[] args) {
        JedisTest48 demo = new JedisTest48();
        //发表一篇博客
        long id = demo.getBlogId();
        Map<String, String> blog = new HashMap<String, String>();
        blog.put("id", String.valueOf(id));
        blog.put("title","我喜欢学习Redis" );
        blog.put("content", "学习Redis是一件特别快乐的事情");
        blog.put("author", "朱磊");
        blog.put("time", "2021-07-18 21:00:00");

        demo.publishBlog(id, blog);

        //更新一篇博客
        Map<String,String> updatedBlog = new HashMap<String,String>();
        updatedBlog.put("title","我特别喜欢学Redis");
        updatedBlog.put("content","我平时喜欢到官方网站上去学习Redis");
        demo.updateBlog(id,updatedBlog);

        //有别人点击进去查看博客的详细内容,并且进行点赞
        Map<String, String> blogResult = demo.finBlogById(id);
        System.out.println("查看博客的详情内容:" + blogResult);
        //点赞
        demo.incrementBloglikeCount(id);

        //自己查看自己的博客，看看浏览次数和点赞次数
        blog = demo.finBlogById(id);
        System.out.println("自己查看博客的详情内容:" + blog);

    }

    /**
     * 发表一篇博客
     */
    public Boolean publishBlog(long id,Map<String,String> blog) {
        if (jedis.hexists("article::" + id, "title")) {
            return false;
        }
        blog.put("content_length", String.valueOf(blog.get("content").length()));
        jedis.hmset("article::" + id, blog);

        return true;
    }


    /**
     * 获取博客（通过id）
     *
     * @param id
     * @return
     */
    public Map<String, String> finBlogById(long id) {

        Map<String, String> blog = jedis.hgetAll("article::" + id);
        incrementBlogViewCount(id);
        return blog;
    }

    /**
     * 更新一篇博客
     */
    public void updateBlog(long id, Map<String, String> updateBlog) {
        String updatedContent = updateBlog.get("content");

        if(updatedContent != null && !"".equals(updatedContent)){
            updateBlog.put("content_length",String.valueOf(updatedContent.length()));
        }

        jedis.hmset("article::" + id, updateBlog);
    }

    /**
     * 对博客进行点赞
     */
    public void incrementBloglikeCount(long id) {
        jedis.hincrBy("article::" + id , "like_count",1);
    }

    /**
     * 增加博客的浏览次数
     *
     * @param id
     */
    public void incrementBlogViewCount(long id) {
        jedis.hincrBy("article::" + id, "view_count", 1);
    }
}
