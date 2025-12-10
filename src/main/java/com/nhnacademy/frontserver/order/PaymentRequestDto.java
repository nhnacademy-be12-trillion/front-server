package com.nhnacademy.frontserver.order;

record PaymentRequestDto(
        String paymentKey,
        String orderNumber,
        Integer amount
) {
}
