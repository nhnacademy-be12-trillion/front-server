package com.nhnacademy.frontserver.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartSummaryResponseDto {
    private long lineCount;      // 종류 수
    private long totalQuantity;  // 전체 수량
}
