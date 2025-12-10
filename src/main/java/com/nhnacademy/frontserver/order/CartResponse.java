package com.nhnacademy.frontserver.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public//널값 필드는 JSON에서 제외
record CartResponse(
        Long memberId,   // 회원일 때만 값 있음
        String guestId,  // 비회원일 때만 값 있음
        Long bookId,
        int cartQuantity,
        LocalDateTime createdAt
) {
}
