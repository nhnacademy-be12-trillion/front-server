package com.nhnacademy.frontserver.cart.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public//널값 필드는 JSON에서 제외
class CartResponseDto {

    private Long memberId;   // 회원일 때만 값 있음
    private String guestId;  // 비회원일 때만 값 있음

    Long bookId;
    int cartQuantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
}