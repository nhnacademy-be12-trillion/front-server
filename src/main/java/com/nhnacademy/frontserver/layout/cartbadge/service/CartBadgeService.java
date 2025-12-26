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
     * - F5 연타... DDOS 등등... 다른데서 방어해야 한다.
     */
    public int getCartCount() {
        try {
            CartSummaryResponseDto summary = cartClient.getCartSummary().getBody();
            return (summary != null) ? (int) summary.getTotalQuantity() : 0;
        } catch (Exception e) {
            log.error("Cart API Error", e);
            return 0;
        }
    }
}