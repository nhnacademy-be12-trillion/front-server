package com.nhnacademy.frontserver.book;

public record ReviewUpdateRequest(
        int reviewRate,
        String reviewContents
) {}
