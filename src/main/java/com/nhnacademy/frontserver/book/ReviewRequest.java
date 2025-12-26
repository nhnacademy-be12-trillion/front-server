package com.nhnacademy.frontserver.book;

public record ReviewRequest(
        Long bookId,
        int reviewRate,
        String reviewContents
) {}
