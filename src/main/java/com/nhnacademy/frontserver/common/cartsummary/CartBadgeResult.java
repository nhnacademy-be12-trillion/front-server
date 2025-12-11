package com.nhnacademy.frontserver.common.cartsummary;

import com.nhnacademy.frontserver.cart.dto.CartSummaryResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Manager -> Interceptor 통신을 위한 결과 래퍼
 * 데이터(DTO)와 쿠키 갱신 필요 시 쿠키 값(String)을 담는다.
 */
@Getter
@RequiredArgsConstructor
public class CartBadgeResult {
    private final CartSummaryResponseDto summary; // 화면에 뿌릴 데이터
    private final String cookieValueToSet;        // 갱신할 쿠키 문자열 (null이면 갱신 안 함)

    // 쿠키를 새로 구워야 하는지 확인
    public boolean needsCookieUpdate() {
        return cookieValueToSet != null;
    }

    // 빈 장바구니 정적 팩토리 메서드
    public static CartBadgeResult empty() {
        return new CartBadgeResult(new CartSummaryResponseDto(0, 0), null);
    }
}