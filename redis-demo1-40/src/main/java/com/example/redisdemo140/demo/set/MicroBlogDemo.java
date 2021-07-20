package com.example.redisdemo140.demo.set;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * 微博案例
 */
public class MicroBlogDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        MicroBlogDemo demo = new MicroBlogDemo();
        demo.flushDB();

        //定义用户id
        long userId = 1;
        long friendId = 2;
        long superStarId = 3;

        //定义关注的关系链
        demo.follow(userId, friendId);
        demo.follow(userId, superStarId);
        demo.follow(friendId, superStarId);

        //明星看看自己被谁关注了
        Set<String> superStarFollowers = demo.getFollowers(superStarId);
        long superStarFollowersCount = demo.getFollowersCount(superStarId);
        System.out.println("明星被哪些人关注了:" + superStarFollowers + ",关注自己的人数为:" + superStarFollowersCount);

        //朋友看看自己被谁关注了
        Set<String> friendFollowers = demo.getFollowers(friendId);
        long friendFollowersCount = demo.getFollowersCount(friendId);

        Set<String> friendFollowerUsers = demo.getFollowerUsers(friendId);
        long friendFollowerUsersCount = demo.getFollowerUsersCount(friendId);


        System.out.println("朋友被哪些人关注了:" + friendFollowers + ",关注自己的人数为:" + friendFollowersCount
        +"朋友关注了哪些人:"+friendFollowerUsers+",关注了多少人:"+friendFollowerUsersCount);

        //查看我自己关注了多少人
        Set<String> myFollowUsers = demo.getFollowerUsers(userId);
        long myFollowUsersCount = demo.getFollowerUsersCount(userId);
        System.out.println("我关注了哪些人:"+myFollowUsers+",我关注的人数是:"+myFollowUsersCount);


    }

    /**
     * 关注别人
     *
     * @param userId
     * @param followUserId
     */
    public void follow(long userId, long followUserId) {
        //关注别人，别人的粉丝列表增加我的id
        jedis.sadd("user::" + followUserId + "::followers", String.valueOf(userId));
        //我的关注列表增加别人的id
        jedis.sadd("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * 取消关注
     *
     * @param userId
     * @param followUserId
     */
    public void unFollow(long userId, long followUserId) {
        //取消关注别人，别人的粉丝列表减少我的id
        jedis.srem("user::" + followUserId + "::followers", String.valueOf(userId));
        //自己的关注列表取消别人的id
        jedis.srem("user::" + userId + "::follow_users", String.valueOf(followUserId));
    }

    /**
     * 查看有那些人关注了自己
     *
     * @param userId
     * @return
     */
    public Set<String> getFollowers(long userId) {
        return jedis.smembers("user::" + userId + "::followers");
    }

    /**
     * 查看关注了自己的人数
     *
     * @param userId
     * @return
     */
    public long getFollowersCount(long userId) {
        return jedis.scard("user::" + userId + "::followers");
    }

    /**
     * 查看自己关注了哪些人
     *
     * @param userId
     * @return
     */
    public Set<String> getFollowerUsers(long userId) {
        return jedis.smembers("user::" + userId + "::follow_users");
    }

    /**
     * 查看自己关注了多少人
     */
    public long getFollowerUsersCount(long userId) {
        return jedis.scard("user::" + userId + "::follow_users");
    }
}
