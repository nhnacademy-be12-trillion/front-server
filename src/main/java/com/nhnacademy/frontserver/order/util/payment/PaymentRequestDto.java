package com.nhnacademy.frontserver.order.util.payment;

public record PaymentRequestDto(
        String paymentKey,  // 토스 등 PG사에서 준 키
        String orderNumber, // 우리 주문 번호
        Integer amount,     // 결제 금액
        String provider     // "TOSS" 등
) {
}