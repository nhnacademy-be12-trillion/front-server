package com.nhnacademy.frontserver.order;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gateway-payment",
        url = "${gateway.url}",
        contextId = "paymentClient")
public interface PaymentClient {
    //TODO 지훈 PaymentRequestDto 확인, API 경로 논의필요, 결제취소 amount 파라미터 논의
//    @PostMapping("/payments")
//    void createPayment(@RequestBody PaymentRequestDto paymentRequestDto);

    @GetMapping("/api/orders/{orderId}/payment")
    PaymentResponse getPayment(@PathVariable Long orderId);

    @DeleteMapping("/api/orders/{orderId}/payment")
    void deletePayment(@PathVariable Long orderId);

}
