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
            // 暂时禁用Topic初始化，避免启动时与RocketMQ通信导致超时
            // Topic会在第一次发送消息时自动创建
            System.out.println("RocketMQ Topic 初始化已禁用，将在首次发送消息时自动创建");
        } catch (Exception e) {
            System.err.println("初始化RocketMQ Topic失败: " + e.getMessage());
        }
    }
}
