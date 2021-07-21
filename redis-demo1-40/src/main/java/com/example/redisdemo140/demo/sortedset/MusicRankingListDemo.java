package com.example.redisdemo140.demo.sortedset;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.Iterator;
import java.util.Set;

/**
 * 案例实战：实现音乐网站的排行榜程序
 * sorted set 不能有重复的数据，加入进去的每个数据都可以带一个分数，它里面的数据都会按照分数进行排序
 * 有序的set，自动按照分数来排序，相当于你可以定制它里面的排序规则
 *
 * zadd 将音乐加入排行榜；
 *
 * zscore 可以获取音乐的分数；
 * zrem 可以删除某个音乐
 * zincrby 可以给某个音乐增加分数，按照实际操作定制规则
 * zrevrank 获取音乐在排行榜中的排名
 * zrevrange set 0 100 withscores 获取前一百首热门歌曲
 */
public class MusicRankingListDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        MusicRankingListDemo demo = new MusicRankingListDemo();
        demo.flushDB();
        for (int i = 0; i < 20; i++) {
            demo.addMusic(i+1);
        }

        demo.incrementMusicScore(5,3.2);
        demo.incrementMusicScore(15,5.6);
        demo.incrementMusicScore(7,9.6);

        long songRank = demo.getSongRank(5);
        System.out.println("查看id为5的歌曲排名:"+songRank);

        Set<Tuple> musicRankingList = demo.getMusicRankingList();
        Iterator<Tuple> iterator = musicRankingList.iterator();
        System.out.println("查看排行榜排名前三的歌曲为:"+musicRankingList);
        while (iterator.hasNext()){
            Tuple next = iterator.next();
            System.out.println(next.getElement()+" "+next.getScore());
        }
    }

    /**
     * 把新音乐加入排行榜中
     * @param songId
     */
    public void addMusic(long songId){
        jedis.zadd("music_ranking_list",0,String.valueOf(songId));
    }

    /**
     * 增加歌曲的分数
     * @param songId
     * @param score
     */
    public void incrementMusicScore(long songId,double score){
        jedis.zincrby("music_ranking_list",score,String.valueOf(songId));
    }

    /**
     * 获取歌曲在排行榜中的排名
     * @param songId
     * @return
     */
    public long getSongRank(long songId){
        return jedis.zrevrank("music_ranking_list",String.valueOf(songId));
    }

    /**
     * 获取音乐排行榜
     */
    public Set<Tuple> getMusicRankingList(){
        Set<Tuple> music_ranking_list = jedis.zrevrangeWithScores("music_ranking_list", 0, 2);
        return music_ranking_list;
    }



}
