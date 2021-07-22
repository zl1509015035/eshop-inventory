package com.example.redisdemo140.demo.expire;

import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Set;

/**
 * 案例实战:网站搜索框的自动补全功能 定时自动过期
 * zrangebylex
 * zrevrangebylex
 * zlexcount
 * zremrangebylex
 * zpopmax
 * zpopmin
 * bzpopmax
 * bzpopmin
 */
public class AutoCompleteDemo {
    private Jedis jedis = new Jedis("192.168.1.10", 6379);

    public void flushDB() {
        jedis.flushDB();
    }

    public static void main(String[] args) throws Exception {
        AutoCompleteDemo demo = new AutoCompleteDemo();
        demo.flushDB();

        demo.search("我爱大家");
        demo.search("我喜欢学习Redis");
        demo.search("我很喜欢一个城市");
        demo.search("我不太喜欢玩儿");
        demo.search("我喜欢学习Spark");

        Set<String> autoCompleteList = demo.getAutoCompleteList("我");
        System.out.println("第一次自动补全推荐:"+autoCompleteList);

        autoCompleteList = demo.getAutoCompleteList("我喜");
        System.out.println("第二次自动补全推荐:"+autoCompleteList);

        Thread.sleep(12*1000);

        autoCompleteList = demo.getAutoCompleteList("我喜");
        System.out.println("第三次自动补全推荐:"+autoCompleteList);

    }

    /**
     * 搜索某个关键词
     * @param keyword
     */
    public void search(String keyword){
        char[] keywordCharArray = keyword.toCharArray();

        StringBuffer potentialKeyword = new StringBuffer();

        //我喜欢学习
        //我：时间+我喜欢学习
        //我喜:时间+我喜欢学习

        for (char keywordChar : keywordCharArray) {
            potentialKeyword.append(keywordChar);
            jedis.zincrby("potential_keyword::"+potentialKeyword+"::keywords",
                    new Date().getTime(),
                    keyword);

            jedis.expire("potential_keyword::"+potentialKeyword+"::keywords",10);

        }
    }

    /**
     * 获取自动补全的列表
     * @param potentialKeyword
     * @return
     */
    public Set<String> getAutoCompleteList(String potentialKeyword){
        return jedis.zrevrange("potential_keyword::"+potentialKeyword+"::keywords",0,2);
    }
}
