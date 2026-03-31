package com.gameitem.trade.domain.repository;

import com.gameitem.trade.domain.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeOrderRepository extends JpaRepository<TradeOrder, Long> {
}
