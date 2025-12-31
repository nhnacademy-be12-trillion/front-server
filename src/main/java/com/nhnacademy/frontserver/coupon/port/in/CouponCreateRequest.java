package com.nhnacademy.frontserver.coupon.port.in;

import java.time.LocalDateTime;

public record CouponCreateRequest(
        String name,
        Long policyId,
        Long quantity,
        LocalDateTime issueStartDate,
        LocalDateTime issueEndDate,
        Long bookId,        // String bookName -> Long bookId 변경
        Long categoryId     // String categoryName -> Long categoryId 변경
) {
}