package com.example.redisdemo140.demo.geo;

import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

/**
 * 案例实战:用户与商家的距离计算案例
 */
public class UserShopDistanceDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        UserShopDistanceDemo demo = new UserShopDistanceDemo();
        demo.flushDB();

        demo.addLocation("张三",116.49428833935545,39.86700462665782);
        demo.addLocation("丫丫小吃店",116.45961274121092,39.87517301328063);

        System.out.println("用户到商家的距离为:"+demo.getDistance("张三","丫丫小吃店"));
    }

    public void addLocation(String name,double longitude,double latitude){
        jedis.geoadd("location_data",longitude,latitude,name);
    }

    public double getDistance(String user,String shop){
        return jedis.geodist("location_data",user,shop, GeoUnit.M);
    }
}
