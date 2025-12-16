package com.nhnacademy.frontserver.layout.cartbadge.service;

import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartBadgeService {

    private final CartClient cartClient;

    /**
     * 장바구니 개수 조회
     * - 캐싱? 그런 거 없다.
     * - 무조건 백엔드에 물어본다. (백엔드 Redis 믿고 간다)
     * - F5 연타? Nginx 형님이 막아줄 것이다.
     */
    public int getCartCount(Long memberId, String guestId) {
        if (memberId == null && (guestId == null || guestId.isEmpty())) return 0;

        try {
            CartSummaryResponseDto summary = cartClient.getCartSummary().getBody();
            return (summary != null) ? (int) summary.getTotalQuantity() : 0;
        } catch (Exception e) {
            log.error("Cart API Error", e);
            return 0;
        }
    }
}