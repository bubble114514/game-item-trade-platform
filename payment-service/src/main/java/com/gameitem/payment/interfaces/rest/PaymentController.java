package com.gameitem.payment.interfaces.rest;

import com.gameitem.common.api.ApiResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @PostMapping("/orders/{orderId}/mock-pay")
    public ApiResult<Map<String, String>> mockPay(@PathVariable Long orderId) {
        String payNo = "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return ApiResult.ok(Map.of("orderId", String.valueOf(orderId), "status", "SUCCESS", "payNo", payNo));
    }
}
