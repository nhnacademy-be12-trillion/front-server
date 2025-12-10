package com.nhnacademy.frontserver.book;

import java.time.LocalDateTime;

public record ReviewResponse (
        Long reviewId,
        int reviewRate,
        String reviewContents,
        LocalDateTime createdAt,
        String writerName
) {
}
