package com.example.redisdemo140.demo.bitmap;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 案例实战:基于位图的网站用户行为记录程序
 * <p>
 * 记录用户在执行一些特殊的操作，每天执行过某个操作的用户有多少
 * 个人，操作日志，审计日志，记录每个用户每天做了哪些操作，每个用户每天搞一个set存放他做的操作日志
 * bitmap 位图，二进制里的一位一位的，字符串，int，long，double，二进制，都是对应多少多少位的
 * 1个字节是的二进制数，int是4个字节->32位二进制数
 * <p>
 * setbit key user_id 1
 * getbit key user_id
 * bitcount key
 */
public class UserOperationLogDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        UserOperationLogDemo demo = new UserOperationLogDemo();
        demo.flushDB();
        demo.recordUsesrOperationLog("操作1",110);
        System.out.println("用户110是否执行过操作:"+(demo.hasOperated("操作1",110)?"是":"否"));

        System.out.println("用户111是否执行过操作:"+(demo.hasOperated("操作1",111)?"是":"否"));



    }

    /**
     * 记录用户的操作日志
     *
     * @param operation
     * @param userId
     */
    public void recordUsesrOperationLog(String operation, long userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        jedis.setbit("operation::" + operation + "::" + today + "::log",
                userId,
                String.valueOf(1));
    }

    /**
     * 判断用户今天是否执行过某个操作
     * @param operation
     * @param userId
     * @return
     */
    public Boolean hasOperated(String operation,long userId){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dateFormat.format(new Date());

        return jedis.getbit("operation::" + operation + "::" + today + "::log",userId);
    }


}
