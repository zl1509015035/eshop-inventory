package com.example.redisdemo140.demo.set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 案例：投票统计
 */
public class VoteDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        VoteDemo demo = new VoteDemo();
        demo.flushDB();
        //定义用户的id
        long userId1 = 1;

        //定义投票项的id
        long voteItemID = 110;

        //进行投票
        demo.vote(userId1,voteItemID);

        //检查我是否投票过
        boolean hasVoted = demo.hasVoted(userId1, voteItemID);
        System.out.println("我是否投票过:"+(hasVoted?"是":"否"));

        //归票统计
        Set<String> voteItemUsers = demo.getVoteItemUsers(voteItemID);
        long voteItemUsersCount = demo.getVoteItemUsersCount(voteItemID);
        System.out.println("投票项有哪些人投票:"+voteItemUsers+",有几个人投票:"+voteItemUsersCount);

    }

    /**
     * 投票
     * @param userId
     * @param voteItemId
     */
    public void vote(long userId, long voteItemId) {
        jedis.sadd("vote_item_users::" + voteItemId, String.valueOf(userId));
    }

    /**
     * 检查用户对投票箱是否投过票
     * @param userId
     * @param voteItemId
     * @return
     */
    public boolean hasVoted(long userId,long voteItemId){
        return jedis.sismember("vote_item_users::"+voteItemId,String.valueOf(userId));
    }

    /**
     * 获取一个投票项被哪些人投票了
     * @param voteItemId
     * @return
     */
    public Set<String> getVoteItemUsers(long voteItemId){
        return jedis.smembers("vote_item_users::"+voteItemId);
    }

    /**
     * 获取一个投票项，被多少人投票了
     */
    public long getVoteItemUsersCount(long voteItemId){
        return jedis.scard("vote_item_users::"+voteItemId);
    }

}
