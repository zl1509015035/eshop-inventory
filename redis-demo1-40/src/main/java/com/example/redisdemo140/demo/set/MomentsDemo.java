package com.example.redisdemo140.demo.set;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 案例：朋友圈点赞
 */
@Slf4j
public class MomentsDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public static void main(String[] args) throws Exception{
        MomentsDemo demo = new MomentsDemo();
        //清掉redis缓存
        demo.flushDB();

        //你的用户id
        long userId = 1;
        //你的朋友圈id
        long momentId = 150;
        //朋友1的id
        long friendId = 2;
        //朋友2的id
        long otherFriendId = 3;

        //你的朋友1对你的朋友圈进行点赞
        demo.likeMoment(friendId,momentId);
        demo.disLikeMoment(friendId,momentId);

        boolean hasLikeMoment = demo.hasLikedMoment(friendId,momentId);
        System.out.println("朋友1刷朋友圈，看到是否对你的朋友圈点赞过:"+(hasLikeMoment?"是":"否"));

        //你的朋友2对你的朋友圈进行点赞
        demo.likeMoment(otherFriendId,momentId);
        hasLikeMoment = demo.hasLikedMoment(otherFriendId,momentId);
        System.out.println("朋友2刷朋友圈，看到是否对你的朋友圈点赞过:"+(hasLikeMoment?"是":"否"));

        //自己刷朋友圈，看自己朋友圈点赞情况
        Set<String> momentLikeUsers = demo.getMomentLikeUsers(momentId);
        long momentLikeUsersCount = demo.getMomentLikeUsersCount(momentId);
        System.out.println("你自己刷朋友圈，看到自己发朋友圈被"+momentLikeUsersCount+"个人点赞了，点赞的用户为:"+momentLikeUsers);

    }

    /**
     * 对朋友圈进行点赞
     *
     * @param userId   朋友的id
     * @param momentId 朋友圈的id
     */
    public void likeMoment(long userId, long momentId) {
        jedis.sadd("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 对朋友圈取消点赞
     *
     * @param userId
     * @param momentId
     */
    public void disLikeMoment(long userId, long momentId) {
        jedis.srem("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 查看自己是否对某条朋友圈点赞过
     *
     * @param userId
     * @param momentId
     * @return
     */
    public boolean hasLikedMoment(long userId, long momentId) {
        return jedis.sismember("moment_like_users::" + momentId, String.valueOf(userId));
    }

    /**
     * 获取一条朋友圈有哪些人进行点赞
     */
    public Set<String> getMomentLikeUsers(long momentId) {
        return jedis.smembers("moment_like_users::" + momentId);
    }

    /**
     * 获取一条朋友圈被几个人点赞了
     * @param momentId
     * @return
     */
    public long getMomentLikeUsersCount(long momentId){
        return jedis.scard("moment_like_users::" + momentId);
    }

    public void flushDB(){
        jedis.flushDB();
    }

}
