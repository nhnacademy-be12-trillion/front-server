package com.nhnacademy.frontserver.book;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long reviewId,
        Long bookId,
        String bookName,
        int reviewRate,
        String reviewContents,
        LocalDateTime createdAt,
        String writerName,
        List<String> imageUrls
) {
    public ReviewResponse withWriterName(String name) {
        return new ReviewResponse(reviewId, bookId, bookName, reviewRate, reviewContents, createdAt, name, imageUrls);
    }
}