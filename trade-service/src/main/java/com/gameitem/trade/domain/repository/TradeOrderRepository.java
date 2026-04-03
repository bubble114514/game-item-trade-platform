package com.gameitem.trade.domain.repository;

import com.gameitem.trade.domain.model.TradeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
    
    Optional<TradeOrder> findByOrderNo(String orderNo);
    
    Page<TradeOrder> findByBuyerId(Long buyerId, Pageable pageable);
    
    Page<TradeOrder> findBySellerId(Long sellerId, Pageable pageable);
    
    Page<TradeOrder> findByBuyerIdAndStatus(Long buyerId, TradeOrder.OrderStatus status, Pageable pageable);
    
    Page<TradeOrder> findBySellerIdAndStatus(Long sellerId, TradeOrder.OrderStatus status, Pageable pageable);
}
