package com.nhnacademy.frontserver.book;

public record ReviewCreateRequest(
        Long orderId,
        Long bookId,
        int reviewRate,
        String reviewContents
) {}
