package com.example.redisdemo140.demo.expire;

import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 案例实战：基于令牌的用户登录回话机制
 * hash
 * <p>
 * 处理用户的任何一个请求前，必须检查请求是否带有令牌，如果携带就去redis里检查
 * 这个令牌是否在redis里合法且有效
 * 如果有session，允许这个请求处理，说明此人之前登陆过系统，登录后才在redis里放了一个有效session
 * 如果没有有效session，此时就会导致用户必须强制被迫扥估
 */
public class SessionDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) throws Exception {
        SessionDemo demo = new SessionDemo();
        demo.flushDB();
        //第一次访问系统，token都是空的
        boolean sessionValid = demo.isSessionValid(null);
        System.out.println("第一次访问系统的session校验结果:"+((sessionValid == true)?"通过":"不通过"));

        //强制性进行登录，获取到token
        String token = demo.login("zhangsan","123456");
        System.out.println("登录后拿到了令牌:"+token);

        //第二次再次访问系统，此时是可以访问的
        sessionValid = demo.isSessionValid(token);
        System.out.println("第二次访问系统的session校验结果:"+((sessionValid == true)?"通过":"不通过"));

        Thread.sleep(12*1000);
        //第三次再次访问系统，此时是可以访问的
        sessionValid = demo.isSessionValid(token);
        System.out.println("第三次访问系统的session校验结果:"+((sessionValid == true)?"通过":"不通过"));
    }

    /**
     * 检查session是否有效
     *
     * @return
     */
    public boolean isSessionValid(String token) throws ParseException {
        if (token == null || "".equals(token)) {
            return false;
        }
        //这里拿到的session可能是一个json字符串
        //放一个用户的user_id作为这里的value
        String session = jedis.get("session::" + token);
        if (session == null || "".equals(session) || "null".equals(session)) {
            return false;
        }
        //如果token不为空，而且获取到session不为空，且session不过期，可放行
        return true;
    }

    /**
     * 用户登录
     */
    public String login(String username, String password) {
        //基于用户名和密码去登录
        System.out.println("基于用户名和密码登录:" + username + "," + password);
        Random random = new Random();
        long userId = random.nextInt() * 100;
        //登录成功后，生成一块令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //基于凌派和用户id去初始化用户的session
        jedis.set("session::" + token, String.valueOf(userId));
        jedis.expire("session::" + token, 10);
        //返回令牌
        return token;
    }

}
