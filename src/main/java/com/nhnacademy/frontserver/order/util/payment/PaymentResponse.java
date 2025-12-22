package com.nhnacademy.frontserver.order.util.payment;

import java.time.LocalDateTime;

public record PaymentResponse (
        String orderNumber,      // 주문 번호
        Integer totalAmount,     // 총 금액
        String status,           // 결제 상태 (DONE, CANCELED 등)
        LocalDateTime requestedAt, // 요청 일시
        LocalDateTime approvedAt,  // 승인 일시
        String provider,         // 결제사 (TOSS 등)
        String receiptUrl        // 영수증 URL
) {
}