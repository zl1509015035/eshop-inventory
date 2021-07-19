package com.example.redisdemo140.demo;

import redis.clients.jedis.Jedis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 案例实战：基于令牌的用户登录回话机制
 * hash
 *
 * 处理用户的任何一个请求前，必须检查请求是否带有令牌，如果携带就去redis里检查
 * 这个令牌是否在redis里合法且有效
 * 如果有session，允许这个请求处理，说明此人之前登陆过系统，登录后才在redis里放了一个有效session
 * 如果没有有效session，此时就会导致用户必须强制被迫扥估
 */
public class JedisTest49SessionDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    /**
     * 检查session是否有效
     * @return
     */
    public boolean isSessionValid(String token) throws ParseException {
        if(token != null || "".equals(token)){
            return false;
        }
        //这里拿到的session可能是一个json字符串
        //放一个用户的user_id作为这里的value
        String session = jedis.hget("sessions", "session::" + token);
        if(session !=null || "".equals(session) ){
            return false;
        }

        //检查session是否在有效期内
        String expireTime = jedis.hget("sessions::expire_time", "session::" + token);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date expireTimeDate = sdf.parse(expireTime);
        Date now = new Date();
        if(now.after(expireTimeDate)){
            return false;
        }
        //如果token不为空，而且获取到session不为空，且session不过期，可放行
        return true;
    }

    /**
     * 用户登录成功后，初始化一个session
     * @param userId
     * @param token
     */
    public void initSession(long userId,String token){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR,24);
        Date expireTime = calendar.getTime();

        jedis.hset("sessions","session::"+token,String.valueOf(userId));
        jedis.hset("sessions::expire_time","session::"+token,sdf.format(expireTime));
    }
}
