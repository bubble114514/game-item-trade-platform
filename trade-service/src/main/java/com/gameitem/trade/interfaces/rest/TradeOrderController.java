package com.gameitem.trade.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import com.gameitem.common.exception.BizException;
import com.gameitem.trade.application.OrderCommandService;
import com.gameitem.trade.domain.model.TradeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/trade")
public class TradeOrderController {

    private final OrderCommandService orderCommandService;

    public TradeOrderController(OrderCommandService orderCommandService) {
        this.orderCommandService = orderCommandService;
    }

    // 基于挂牌创建一口价订单（推荐使用此接口）
    @PostMapping("/orders/fixed/listing")
    public ApiResult<TradeOrder> createFixedOrderByListing(@RequestBody CreateFixedOrderByListingReq req) {
        TradeOrder order = orderCommandService.createFixedOrderByListing(
            req.listingId(), req.buyerId(), req.quantity()
        );
        return ApiResult.ok(order);
    }

    // 创建一口价订单（直接指定价格和数量）
    @PostMapping("/orders/fixed")
    public ApiResult<TradeOrder> createFixedOrder(@RequestBody CreateFixedOrderReq req) {
        TradeOrder order = orderCommandService.createFixedOrder(
            req.buyerId(), req.sellerId(), req.itemId(), 
            req.quantity(), req.unitPrice()
        );
        return ApiResult.ok(order);
    }

    // 创建挂牌交易订单
    @PostMapping("/orders/listing")
    public ApiResult<TradeOrder> createListingOrder(@RequestBody CreateListingOrderReq req) {
        TradeOrder order = orderCommandService.createListingOrder(
            req.buyerId(), req.sellerId(), req.itemId(), req.listingId(),
            req.quantity(), req.unitPrice()
        );
        return ApiResult.ok(order);
    }

    // 支付订单
    @PostMapping("/orders/{orderId}/pay")
    public ApiResult<TradeOrder> payOrder(@PathVariable Long orderId, @RequestParam Long id) {
        return ApiResult.ok(orderCommandService.payOrder(orderId, id));
    }

    // 发货
    @PostMapping("/orders/{orderId}/deliver")
    public ApiResult<TradeOrder> deliverOrder(@PathVariable Long orderId, @RequestParam Long id) {
        return ApiResult.ok(orderCommandService.deliverOrder(orderId, id));
    }

    // 确认收货
    @PostMapping("/orders/{orderId}/complete")
    public ApiResult<TradeOrder> completeOrder(@PathVariable Long orderId, @RequestParam Long id) {
        return ApiResult.ok(orderCommandService.completeOrder(orderId, id));
    }

    // 取消订单
    @PostMapping("/orders/{orderId}/cancel")
    public ApiResult<TradeOrder> cancelOrder(@PathVariable Long orderId, @RequestParam Long id) {
        return ApiResult.ok(orderCommandService.cancelOrder(orderId, id));
    }

    // 查询订单
    @GetMapping("/orders/{orderId}")
    public ApiResult<TradeOrder> getOrder(@PathVariable Long orderId) {
        return ApiResult.ok(orderCommandService.getOrderById(orderId));
    }

    @GetMapping("/orders/no/{orderNo}")
    public ApiResult<TradeOrder> getOrderByNo(@PathVariable String orderNo) {
        return ApiResult.ok(orderCommandService.getOrderByNo(orderNo));
    }

    // 查询买家订单
    @GetMapping("/orders/buyer/{buyerId}")
    public ApiResult<Page<TradeOrder>> listBuyerOrders(
            @PathVariable Long buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(orderCommandService.listBuyerOrders(buyerId, pageable));
    }

    // 查询卖家订单
    @GetMapping("/orders/seller/{sellerId}")
    public ApiResult<Page<TradeOrder>> listSellerOrders(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResult.ok(orderCommandService.listSellerOrders(sellerId, pageable));
    }

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBiz(BizException e) {
        return ApiResult.fail(e.getCode(), e.getMessage());
    }

    // 请求记录
    public record CreateFixedOrderReq(Long buyerId, Long sellerId, Long itemId,
                                      Integer quantity, BigDecimal unitPrice) {}

    public record CreateFixedOrderByListingReq(Long listingId, Long buyerId, Integer quantity) {}

    public record CreateListingOrderReq(Long buyerId, Long sellerId, Long itemId, Long listingId,
                                       Integer quantity, BigDecimal unitPrice) {}

    public record IdWrapper(Long id) {}
}
