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
        String userName,    // [추가] 실제 작성자 이름 (실명)
        String writerName,  // 기존 필드 (백업용)
        List<String> imageUrls
) {
    // 필드가 추가되었으므로 생성자/메서드 시그니처도 맞춰줍니다.
    public ReviewResponse withWriterName(String name) {
        return new ReviewResponse(reviewId, memberId, bookId, bookName, reviewRate, reviewContents, createdAt, name, writerName, imageUrls);
    }
}