package com.nhnacademy.frontserver.layout.badge;

import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartSummaryResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartBadgeService {

    private final CartClient cartClient;

    /**
     * 장바구니 개수 조회
     * - 인증 쿠키(accessToken, guestId)가 없으면 API 호출을 스킵하여 에러 방지
     */
    public int getCartCount() {
        // 인증 정보가 하나도 없으면 0 리턴
        if (!hasAuthCookie()) {
            return 0;
        }

        try {
            CartSummaryResponseDto summary = cartClient.getCartSummary().getBody();
            return (summary != null) ? (int) summary.getTotalQuantity() : 0;
        } catch (Exception e) {
            // 실제 API 장애나, 쿠키는 있는데 유효하지 않은 경우만 로그 기록
            log.warn("Cart API Error: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 현재 요청에 'accessToken' 또는 'guestId' 쿠키가 있는지 확인
     */
    private boolean hasAuthCookie() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 웹 요청이 아닌 경우(null) 안전하게 false 처리
        if (attributes == null) {
            return false;
        }

        HttpServletRequest request = attributes.getRequest();
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                .anyMatch(cookie -> "accessToken".equals(cookie.getName())
                        || "guestId".equals(cookie.getName()));
    }
}