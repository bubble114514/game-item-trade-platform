package com.gameitem.trade.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import com.gameitem.trade.application.OrderCommandService;
import com.gameitem.trade.domain.model.TradeOrder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/trade")
public class TradeOrderController {

    private final OrderCommandService orderCommandService;

    public TradeOrderController(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    public record CreateOrderReq(Long buyerId, Long sellerId, Long itemId, BigDecimal amount) {}

    @PostMapping("/orders")
    public ApiResult<TradeOrder> create(@RequestBody CreateOrderReq req) {
        TradeOrder o = orderCommandService.createOrder(req.buyerId(), req.sellerId(), req.itemId(), req.amount());
        return ApiResult.ok(o);
    }
}
