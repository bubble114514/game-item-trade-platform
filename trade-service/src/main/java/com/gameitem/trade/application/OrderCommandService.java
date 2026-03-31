package com.gameitem.trade.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameitem.trade.domain.model.TradeOrder;
import com.gameitem.trade.domain.repository.TradeOrderRepository;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderCommandService {

    public static final String TOPIC_ORDER = "ORDER_TOPIC";
    public static final String TAG_CREATED = "CREATED";

    private final TradeOrderRepository tradeOrderRepository;
    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderCommandService(TradeOrderRepository tradeOrderRepository, RocketMQTemplate rocketMQTemplate) {
        this.tradeOrderRepository = tradeOrderRepository;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Transactional
    public TradeOrder createOrder(Long buyerId, Long sellerId, Long itemId, BigDecimal amount) {
        TradeOrder order = new TradeOrder();
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setItemId(itemId);
        order.setAmount(amount);
        TradeOrder saved = tradeOrderRepository.save(order);
        try {
            String json = objectMapper.writeValueAsString(new OrderCreatedEvent(saved.getId(), buyerId, sellerId, itemId, amount));
            rocketMQTemplate.syncSend(TOPIC_ORDER + ":" + TAG_CREATED,
                    MessageBuilder.withPayload(json.getBytes()).build());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return saved;
    }

    public record OrderCreatedEvent(Long orderId, Long buyerId, Long sellerId, Long itemId, BigDecimal amount) {}
}
