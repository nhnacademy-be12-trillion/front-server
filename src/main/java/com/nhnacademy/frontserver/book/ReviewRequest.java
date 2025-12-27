package com.nhnacademy.frontserver.book;

public record ReviewRequest(
        Long orderId,
        Long bookId,
        int reviewRate,
        String reviewContents
) {}
