package com.example.redisdemo140.demo.geo;

import redis.clients.jedis.GeoRadiusResponse;
import redis.clients.jedis.GeoUnit;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * 案例实战：查找附近人的案例
 */
public class NearbyShopsDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) {
        NearbyShopsDemo demo = new NearbyShopsDemo();

        List<String> nearbyShops = new ArrayList<String>();

        List<GeoRadiusResponse> result = demo.getNewrbyUser();
        for (GeoRadiusResponse nearbyShop : result) {
            String shopName = nearbyShop.getMemberByString();
            if(!shopName.equals("张三"))
                nearbyShops.add(shopName);
        }
        System.out.println("附近5公里内的商家:" + nearbyShops);
    }

    /**
     * 增加位置
     *
     * @param name
     * @param longitude
     * @param latitude
     */
    public void addLocation(String name, double longitude, double latitude) {
        jedis.geoadd("location_data", longitude, latitude, name);
    }

    /**
     * 查找附近5公里的店铺
     *
     * @return
     */
    public List<GeoRadiusResponse> getNewrbyUser() {
        return jedis.georadiusByMember("location_data", "张三", 5, GeoUnit.KM);
    }
}
