package com.gameitem.trade.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameitem.common.exception.BizException;
import com.gameitem.trade.domain.model.TradeOrder;
import com.gameitem.trade.domain.repository.TradeOrderRepository;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderCommandService {

    public static final String TOPIC_ORDER = "ORDER_TOPIC";
    public static final String TAG_CREATED = "CREATED";
    public static final String TAG_PAID = "PAID";
    public static final String TAG_DELIVERED = "DELIVERED";
    public static final String TAG_COMPLETED = "COMPLETED";
    public static final String TAG_CANCELLED = "CANCELLED";

    private final TradeOrderRepository tradeOrderRepository;
    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderCommandService(TradeOrderRepository tradeOrderRepository, RocketMQTemplate rocketMQTemplate) {
        this.tradeOrderRepository = tradeOrderRepository;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    /**
     * 创建一口价订单
     */
    @Transactional
    public TradeOrder createFixedOrder(Long buyerId, Long sellerId, Long itemId, 
                                       Integer quantity, BigDecimal unitPrice) {
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }
        if (unitPrice == null) {
            throw new BizException(40001, "单价不能为空");
        }
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        TradeOrder order = new TradeOrder();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setItemId(itemId);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setTradeMode(TradeOrder.TradeMode.FIXED);
        order.setStatus(TradeOrder.OrderStatus.CREATED);
        
        TradeOrder saved = tradeOrderRepository.save(order);
        
        // 发送订单创建消息
        sendOrderEvent(saved, TAG_CREATED);
        
        return saved;
    }

    /**
     * 基于挂牌创建一口价订单
     */
    @Transactional
    public TradeOrder createFixedOrderByListing(Long listingId, Long buyerId, Integer quantity) {
        // 这里应该调用道具服务获取挂牌信息
        // 暂时模拟数据
        Long sellerId = 1L;
        Long itemId = 1L;
        BigDecimal unitPrice = BigDecimal.valueOf(120);
        
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        TradeOrder order = new TradeOrder();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setItemId(itemId);
        order.setListingId(listingId);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setTradeMode(TradeOrder.TradeMode.FIXED);
        order.setStatus(TradeOrder.OrderStatus.CREATED);

        TradeOrder saved = tradeOrderRepository.save(order);

        // 发送订单创建消息
        sendOrderEvent(saved, TAG_CREATED);

        return saved;
    }

    /**
     * 创建挂牌交易订单
     */
    @Transactional
    public TradeOrder createListingOrder(Long buyerId, Long sellerId, Long itemId, Long listingId,
                                         Integer quantity, BigDecimal unitPrice) {
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));
        
        TradeOrder order = new TradeOrder();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setItemId(itemId);
        order.setListingId(listingId);
        order.setQuantity(quantity);
        order.setUnitPrice(unitPrice);
        order.setTotalAmount(totalAmount);
        order.setTradeMode(TradeOrder.TradeMode.LISTING);
        order.setStatus(TradeOrder.OrderStatus.CREATED);
        
        TradeOrder saved = tradeOrderRepository.save(order);
        
        // 发送订单创建消息
        sendOrderEvent(saved, TAG_CREATED);
        
        return saved;
    }

    /**
     * 支付订单
     */
    @Transactional
    public TradeOrder payOrder(Long orderId, Long buyerId) {
        TradeOrder order = tradeOrderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(40401, "订单不存在"));
        
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BizException(40301, "无权操作此订单");
        }
        
        if (order.getStatus() != TradeOrder.OrderStatus.CREATED) {
            throw new BizException(40001, "订单状态不允许支付");
        }
        
        order.setStatus(TradeOrder.OrderStatus.PAID);
        order.setPaidAt(Instant.now());
        
        TradeOrder saved = tradeOrderRepository.save(order);
        
        // 发送支付消息
        sendOrderEvent(saved, TAG_PAID);
        
        return saved;
    }

    /**
     * 发货
     */
    @Transactional
    public TradeOrder deliverOrder(Long orderId, Long sellerId) {
        TradeOrder order = tradeOrderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(40401, "订单不存在"));
        
        if (!order.getSellerId().equals(sellerId)) {
            throw new BizException(40301, "无权操作此订单");
        }
        
        if (order.getStatus() != TradeOrder.OrderStatus.PAID) {
            throw new BizException(40001, "订单状态不允许发货");
        }
        
        order.setStatus(TradeOrder.OrderStatus.DELIVERED);

        TradeOrder saved = tradeOrderRepository.save(order);

        sendOrderEvent(saved, TAG_DELIVERED);

        return saved;
    }

    /**
     * 确认收货，完成订单
     */
    @Transactional
    public TradeOrder completeOrder(Long orderId, Long buyerId) {
        TradeOrder order = tradeOrderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(40401, "订单不存在"));
        
        if (!order.getBuyerId().equals(buyerId)) {
            throw new BizException(40301, "无权操作此订单");
        }
        
        if (order.getStatus() != TradeOrder.OrderStatus.DELIVERED) {
            throw new BizException(40001, "订单状态不允许完成");
        }
        
        order.setStatus(TradeOrder.OrderStatus.COMPLETED);
        order.setCompletedAt(Instant.now());
        
        TradeOrder saved = tradeOrderRepository.save(order);
        
        // 发送完成消息
        sendOrderEvent(saved, TAG_COMPLETED);
        
        return saved;
    }

    /**
     * 取消订单
     */
    @Transactional
    public TradeOrder cancelOrder(Long orderId, Long userId) {
        TradeOrder order = tradeOrderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(40401, "订单不存在"));
        
        // 买家或卖家都可以取消
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BizException(40301, "无权操作此订单");
        }
        
        // 只有已创建或已支付的订单可以取消
        if (order.getStatus() != TradeOrder.OrderStatus.CREATED 
                && order.getStatus() != TradeOrder.OrderStatus.PAID) {
            throw new BizException(40001, "订单状态不允许取消");
        }
        
        order.setStatus(TradeOrder.OrderStatus.CANCELLED);

        TradeOrder saved = tradeOrderRepository.save(order);

        sendOrderEvent(saved, TAG_CANCELLED);

        return saved;
    }

    // 查询方法
    public TradeOrder getOrderById(Long orderId) {
        return tradeOrderRepository.findById(orderId)
                .orElseThrow(() -> new BizException(40401, "订单不存在"));
    }
    
    public TradeOrder getOrderByNo(String orderNo) {
        return tradeOrderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new BizException(40401, "订单不存在"));
    }
    
    public Page<TradeOrder> listBuyerOrders(Long buyerId, Pageable pageable) {
        return tradeOrderRepository.findByBuyerId(buyerId, pageable);
    }
    
    public Page<TradeOrder> listSellerOrders(Long sellerId, Pageable pageable) {
        return tradeOrderRepository.findBySellerId(sellerId, pageable);
    }

    // 生成订单号
    private String generateOrderNo() {
        String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                .format(java.time.LocalDateTime.now());
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "O" + timestamp + random;
    }

    // 发送订单事件消息
    private void sendOrderEvent(TradeOrder order, String tag) {
        sendOrderEvent(order, tag, "道具");
    }

    private void sendOrderEvent(TradeOrder order, String tag, String itemName) {
        try {
            String json = objectMapper.writeValueAsString(new OrderEvent(
                order.getId(),
                order.getOrderNo(),
                order.getBuyerId(),
                order.getSellerId(),
                order.getItemId(),
                order.getListingId(),
                order.getTotalAmount(),
                order.getStatus().name(),
                order.getTradeMode().name(),
                itemName
            ));
            try {
                rocketMQTemplate.asyncSend(TOPIC_ORDER + ":" + tag,
                        MessageBuilder.withPayload(json.getBytes()).build(), new org.apache.rocketmq.client.producer.SendCallback() {
                            @Override
                            public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                                System.out.println("订单事件消息发送成功: " + sendResult);
                            }

                            @Override
                            public void onException(java.lang.Throwable e) {
                                System.err.println("订单事件消息发送失败: " + e.getMessage());
                            }
                        });
            } catch (Exception e) {
                System.err.println("RocketMQ发送异常: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("发送订单事件消息失败: " + e.getMessage());
        }
    }

    public record OrderEvent(Long orderId, String orderNo, Long buyerId, Long sellerId,
                            Long itemId, Long listingId, BigDecimal amount,
                            String status, String tradeMode, String itemName) {}
}
