package com.nhnacademy.frontserver.book;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long reviewId,
        Long memberId,      // [추가] 작성자 본인 확인용
        Long bookId,
        String bookName,
        int reviewRate,
        String reviewContents,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String userName,    // 실제 작성자 이름 (실명)
        String writerName,  // 기존 필드 (백업용)
        List<String> imageUrls
) {
    public ReviewResponse withWriterName(String name) {
        return new ReviewResponse(reviewId, memberId, bookId, bookName, reviewRate, reviewContents, createdAt, updatedAt, name, writerName, imageUrls);
    }
}