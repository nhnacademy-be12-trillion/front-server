package com.nhnacademy.frontserver.order;

public record CheckoutItemView(
        Long bookId,
        String title,
        String thumbnailUrl,
        int unitPrice,
        int quantity,
        int totalPrice
) {}

