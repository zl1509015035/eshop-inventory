package com.example.redisdemo140.demo.list;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 案例实战：网站用户注册时的邮件验证机制
 */
@Slf4j
public class SendEmailDemo {
    private Jedis jedis = new Jedis("192.168.1.10",6379);

    public static void main(String[] args) {
        SendEmailDemo demo = new SendEmailDemo();
        log.info("尝试阻塞式的获取发送邮件任务.....");
        List<String> sendMailTasks = demo.taskSendMailTask();

        demo.enqueueSendMailTask("第一个邮件发送任务");
        sendMailTasks = demo.taskSendMailTask();
        System.out.println(sendMailTasks);
    }

    /**
     * 让发送邮件任务入队列
     * @param sendMailTask
     */
    public void enqueueSendMailTask(String sendMailTask){
        jedis.lpush("send_mail_task_queue",sendMailTask);
    }

    /**
     * 阻塞式获取发送邮件任务
     * @return
     */
    public List<String> taskSendMailTask(){
        return jedis.brpop(5,"send_mail_task_queue");
    }


}
