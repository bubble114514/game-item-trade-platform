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
        consumerGroup = "message-order-completed-consumer",
        selectorExpression = "COMPLETED"
)
public class OrderCompletedListener implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(OrderCompletedListener.class);

    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderCompletedListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onMessage(String json) {
        log.info("收到订单完成事件：{}", json);
        try {
            JsonNode root = objectMapper.readTree(json);

            Long orderId = root.path("orderId").asLong();
            Long buyerId = root.path("buyerId").asLong();
            Long sellerId = root.path("sellerId").asLong();
            String orderNo = root.path("orderNo").asText();
            String itemName = root.path("itemName").asText("道具");

            messageService.sendMessage(
                    buyerId,
                    "ORDER_COMPLETED",
                    "交易完成",
                    String.format("您购买的 %s 已确认收货，订单 %s 完成。", itemName, orderNo),
                    orderId,
                    sellerId
            );

            messageService.sendMessage(
                    sellerId,
                    "ORDER_COMPLETED",
                    "交易完成",
                    String.format("买家已确认收货，订单 %s 完成，交易成功。", orderNo),
                    orderId,
                    buyerId
            );

            log.info("订单 {} 完成消息发送成功", orderId);
        } catch (Exception e) {
            log.error("处理订单完成消息失败", e);
        }
    }
}