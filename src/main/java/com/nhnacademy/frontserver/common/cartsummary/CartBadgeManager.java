package com.nhnacademy.frontserver.common.cartsummary;

import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartBadgeManager {

    private final ObjectProvider<CartClient> cartClientProvider;
    private final CartSummaryCookieSignedSerializer serializer;

    public CartBadgeResult resolveBadge(String rawCookieValue) {
        // 1. 쿠키 검증 (Cache Hit)
        CartSummaryResponseDto cachedData = serializer.decrypt(rawCookieValue, CartSummaryResponseDto.class);
        if (cachedData != null) {
            return new CartBadgeResult(cachedData, null);
        }

        // 2. API 호출 (DB Source of Truth)
        try {
            CartClient client = cartClientProvider.getIfAvailable();
            if (client != null) {
                // API 호출 시도 (400 에러 등이 나면 catch로 빠짐)
                CartSummaryResponseDto apiData = client.getCartSummary().getBody();
                if (apiData != null) {
                    String newSignedCookie = serializer.encrypt(apiData);
                    return new CartBadgeResult(apiData, newSignedCookie);
                }
            }
        } catch (Exception e) {
            // 로그는 찍되, 서비스가 죽지 않게 무시
            log.warn("장바구니 API 호출 실패 (비로그인 상태 등): {}", e.getMessage());
        }

        // 3. 실패 시 빈 깡통 반환 (Builder 패턴 사용)
        CartSummaryResponseDto emptyDto = CartSummaryResponseDto.builder()
                .lineCount(0)
                .totalQuantity(0)
                .build();

        return new CartBadgeResult(emptyDto, null);
    }
}