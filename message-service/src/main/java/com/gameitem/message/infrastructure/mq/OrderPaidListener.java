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
        consumerGroup = "message-order-paid-consumer",
        selectorExpression = "PAID"
)
public class OrderPaidListener implements RocketMQListener<String> {

    private static final Logger log = LoggerFactory.getLogger(OrderPaidListener.class);

    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderPaidListener(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void onMessage(String json) {
        log.info("收到订单支付事件：{}", json);
        try {
            JsonNode root = objectMapper.readTree(json);

            Long orderId = root.path("orderId").asLong();
            Long buyerId = root.path("buyerId").asLong();
            Long sellerId = root.path("sellerId").asLong();
            String orderNo = root.path("orderNo").asText();
            String itemName = root.path("itemName").asText("道具");

            messageService.sendMessage(
                    sellerId,
                    "ORDER_PAID",
                    "订单已支付",
                    String.format("买家已支付订单 %s，购买 %s，请尽快发货。", orderNo, itemName),
                    orderId,
                    buyerId
            );

            messageService.sendMessage(
                    buyerId,
                    "ORDER_PAID",
                    "支付成功",
                    String.format("您的订单 %s 已支付成功，等待卖家发货。", orderNo),
                    orderId,
                    sellerId
            );

            log.info("订单 {} 支付消息发送成功", orderId);
        } catch (Exception e) {
            log.error("处理订单支付消息失败", e);
        }
    }
}