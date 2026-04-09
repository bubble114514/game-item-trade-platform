package com.gameitem.trade.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

    private static final Logger logger = LoggerFactory.getLogger(RocketMQConfig.class);

    @Value("${rocketmq.name-server}")
    private String nameServer;

    @Value("${rocketmq.producer.group}")
    private String producerGroup;

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        logger.info("创建DefaultMQProducer，group: {}", producerGroup);
        logger.info("RocketMQ name-server: {}", nameServer);
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setSendMsgTimeout(15000);
        producer.setRetryTimesWhenSendFailed(3);
        logger.info("DefaultMQProducer创建完成");
        return producer;
    }

    @Bean
    public RocketMQTemplate rocketMQTemplate(DefaultMQProducer defaultMQProducer) {
        logger.info("创建RocketMQTemplate");
        RocketMQTemplate rocketMQTemplate = new RocketMQTemplate();
        rocketMQTemplate.setProducer(defaultMQProducer);
        logger.info("RocketMQTemplate创建完成");
        return rocketMQTemplate;
    }
}
