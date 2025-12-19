package com.nhnacademy.frontserver.order.util.payment;

public record PaymentCancelRequestDto(
        String orderNumber,  // 주문 번호
        String cancelReason, // 취소 사유
        Integer cancelAmount // 취소 금액 (부분 취소 가능성 고려)
) {
}