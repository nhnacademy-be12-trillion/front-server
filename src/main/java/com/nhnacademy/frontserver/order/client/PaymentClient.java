package com.nhnacademy.frontserver.order.client;
import com.nhnacademy.frontserver.order.util.payment.PaymentCancelRequestDto;
import com.nhnacademy.frontserver.order.util.payment.PaymentRequestDto;
import com.nhnacademy.frontserver.order.util.payment.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-payment",
        url = "${gateway.url}",
        contextId = "paymentClient")
public interface PaymentClient {
    @PostMapping("/api/payments/confirm")
    PaymentResponse confirmPayment(@RequestBody PaymentRequestDto paymentRequestDto);

    /**
     * 결제 내역 단건 조회 (Controller: GET /payments/{orderNumber})
     * Controller에서 PathVariable이 String orderNumber이므로 타입과 변수명 일치시킴
     */
    @GetMapping("/api/payments/{orderNumber}")
    PaymentResponse getPayment(@PathVariable("orderNumber") String orderNumber);

    /**
     * 결제 취소 요청 (Controller: POST /payments/cancel)
     * Controller가 @RequestBody로 DTO를 받으므로 DELETE가 아니라 POST여야 함
     */
    @PostMapping("/api/payments/cancel")
    void cancelPayment(@RequestBody PaymentCancelRequestDto cancelRequestDto);

}
