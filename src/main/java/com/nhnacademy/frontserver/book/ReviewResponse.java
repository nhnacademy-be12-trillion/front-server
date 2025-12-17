package com.nhnacademy.frontserver.book;

import java.time.LocalDateTime;

// [수정됨] 도서 서버의 응답 필드명(writerName, reviewRate, reviewContents)에 맞춤
public record ReviewResponse(
        Long reviewId,
        int reviewRate,         // 기존 score -> reviewRate
        String reviewContents,  // 기존 content -> reviewContents
        LocalDateTime createdAt,
        String writerName       // 기존 reviewerName -> writerName
) {
}