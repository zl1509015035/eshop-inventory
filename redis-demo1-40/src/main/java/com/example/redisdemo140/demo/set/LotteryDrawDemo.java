package com.example.redisdemo140.demo.set;

import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 抽奖案例
 */
public class LotteryDrawDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        LotteryDrawDemo demo = new LotteryDrawDemo();
        demo.flushDB();
        int lotteryDrawEventId = 120;

        for (int i = 0; i < 20; i++) {
            demo.addLotteryDrawCandidate(i + 1, lotteryDrawEventId);
        }

        List<String> lotteryDrawUsers = demo.doLotteryDraw(lotteryDrawEventId, 3);
        System.out.println("获奖人选为:" + lotteryDrawUsers);
    }

    /**
     * 添加
     *
     * @Param userId
     */
    public void addLotteryDrawCandidate(long userId, long lotteryDrawEventId) {
        jedis.sadd("lottery_draw::" + lotteryDrawEventId + "::candidates", String.valueOf(userId));
    }

    /**
     * 实际进行抽奖
     *
     * @param lotteryDrawEventId
     * @return
     */
    public List<String> doLotteryDraw(long lotteryDrawEventId, int count) {
        return jedis.srandmember("lottery_draw::" + lotteryDrawEventId + "::candidates", count);
    }
}
