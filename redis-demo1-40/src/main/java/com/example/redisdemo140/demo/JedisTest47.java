package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 对博客网站案例进行重构 -博客网站2.0
 */
public class JedisTest47 {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public long getBlogId(){
//        jedis.flushDB();
        Long orderIdCounter = jedis.incr("order_id_counter");
        return orderIdCounter;
    }

    public static void main(String[] args) {
        JedisTest47 demo = new JedisTest47();
        //发表一篇博客
        long id = demo.getBlogId();
        String title = "我喜欢学习Redis";
        String content = "学习Redis是一件特别快乐的事情";
        String author = "朱磊";
        String time = "2021-07-18 21:00:00";

        demo.publishBlog(id,title,content,author,time);

        //更新一篇博客
        String updatedTitle = "更新后的"+title;
        String updatedContent = "更新后的"+content;
        demo.updateBlog(id,updatedTitle,updatedContent);

        //搜索博客，看到预览内容
        String previewContent = demo.getPreviewBlog(id);
        System.out.println("看到博客的预览内容:"+previewContent);

        //点击进去查看博客的详细内容,并且进行点赞
        List<String> blog = demo.getBolg(id);
        System.out.println("查看博客的详情内容:"+blog);
        demo.likeBlog(id);

        //自己查看自己的博客，看看浏览次数和点赞次数
        blog = demo.getBolg(id);
        System.out.println("自己查看博客的详情内容:"+blog);

    }

    /**
     * 发表一篇博客
     */
    public void publishBlog(long id, String title, String content, String author, String time) {
        //博客的发布、修改、查看
        jedis.msetnx("article:" + id + ":title", title,
                "article:" + id + ":content", content,
                "article:" + id + ":author", author,
                "article:" + id + ":time", time);

        Long strlen = jedis.strlen("article:" + id + ":content");
        jedis.setnx("article:" + id + ":content_length", String.valueOf(strlen));
    }

    /**
     * 获取博客（通过id）
     *
     * @param id
     * @return
     */
    public List<String> getBolg(long id) {
        List<String> blog = jedis.mget("article:" + id + ":title",
                "article:" + id + ":content",
                "article:" + id + ":author",
                "article:" + id + ":time",
                "article:" + id + ":content_length",
                "article:"+id+":like_count",
                "article:"+id+":view_count"
        );
        blog.add(String.valueOf(id));
        viewBlog(id);
        return blog;
    }

    /**
     * 更新一篇博客
     */
    public void updateBlog(long id, String title, String content) {
        jedis.mset("article:" + id + ":title", title,
                "article:" + id + ":content", content);
        Long strlen = jedis.strlen("article:" + id + ":content");
        jedis.set("article:" + id + ":content_length", String.valueOf(strlen));
    }

    /**
     * 预览博客，搜索博客结果页面里，对每隔搜索结果都是看博客的预览内容
     */
    public String getPreviewBlog(long id){
        String getrange = jedis.getrange("article:" + id + ":content", 0, 12);
        return getrange;
    }

    /**
     * 对博客进行点赞
     */
    public void likeBlog(long id){
        jedis.incr("article:"+id+":like_count");
    }

    /**
     * 增加博客的浏览次数
     * @param id
     */
    public void viewBlog(long id){
        jedis.incr("article:"+id+":view_count");
    }
}
