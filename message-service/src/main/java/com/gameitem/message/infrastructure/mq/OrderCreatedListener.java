package com.gameitem.message.infrastructure.mq;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RocketMQMessageListener(
        topic = "ORDER_TOPIC",
        consumerGroup = "message-order-consumer",
        selectorExpression = "CREATED"
)
public class OrderCreatedListener implements RocketMQListener<byte[]> {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @Override
    public void onMessage(byte[] message) {
        String json = new String(message, StandardCharsets.UTF_8);
        log.info("收到订单创建事件，异步处理：{}", json);
    }
}
