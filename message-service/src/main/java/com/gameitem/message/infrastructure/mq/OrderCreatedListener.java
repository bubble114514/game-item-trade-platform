package com.gameitem.message.infrastructure.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameitem.message.application.MessageService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(
        topic = "ORDER_TOPIC",
        consumerGroup = "message-order-consumer",
        selectorExpression = "CREATED"
)
public class OrderCreatedListener implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderCreatedListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onMessage(String json) {
        log.info("收到订单创建事件：{}", json);
        try {
            JsonNode root = objectMapper.readTree(json);

            Long orderId = root.path("orderId").asLong();
            Long buyerId = root.path("buyerId").asLong();
            Long sellerId = root.path("sellerId").asLong();
            String orderNo = root.path("orderNo").asText();
            String itemName = root.path("itemName").asText("道具");

            messageService.sendMessage(
                    buyerId,
                    "ORDER_CREATED",
                    "订单已创建",
                    String.format("您已成功创建订单 %s，购买 %s，请尽快完成支付。", orderNo, itemName),
                    orderId,
                    sellerId
            );

            messageService.sendMessage(
                    sellerId,
                    "ORDER_CREATED",
                    "有新订单",
                    String.format("您有新订单 %s，买家购买 %s，请等待买家支付。", orderNo, itemName),
                    orderId,
                    buyerId
            );

            log.info("订单 {} 消息发送成功", orderId);
        } catch (Exception e) {
            log.error("处理订单创建消息失败", e);
        }
    }
}