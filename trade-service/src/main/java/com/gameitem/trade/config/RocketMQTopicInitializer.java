package com.gameitem.trade.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RocketMQTopicInitializer implements ApplicationRunner {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            // 获取底层的Producer
            DefaultMQProducer producer = rocketMQTemplate.getProducer();
            
            // 创建Topic
            String topic = "ORDER_TOPIC";
            int queueNum = 4; // 队列数量
            
            // 使用MQAdminExt创建Topic
            try {
                // 尝试发送一条测试消息来触发Topic创建
                rocketMQTemplate.convertAndSend(topic + ":INIT", "Topic Init Message");
                System.out.println("RocketMQ Topic " + topic + " 创建成功");
            } catch (Exception e) {
                System.err.println("RocketMQ Topic " + topic + " 创建失败: " + e.getMessage());
                // 不影响应用启动，Topic会在第一次发送消息时自动创建
            }
        } catch (Exception e) {
            System.err.println("初始化RocketMQ Topic失败: " + e.getMessage());
        }
    }
}
