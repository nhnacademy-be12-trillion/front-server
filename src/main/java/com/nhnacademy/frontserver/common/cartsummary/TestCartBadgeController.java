package com.nhnacademy.frontserver.common.cartsummary;

import com.nhnacademy.frontserver.cart.dto.CartSummaryResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@Slf4j
@Controller
@RequestMapping("/test/cart/badge")
@RequiredArgsConstructor
public class TestCartBadgeController {

    private final CartSummaryCookieSignedSerializer cookieSerializer;

    @GetMapping
    public String viewTestPage() {
        return "test-cart-badge";
    }

    // 가짜 담기 (테스트용)
    @PostMapping("/add")
    @ResponseBody
    public String mockAddToCart(@RequestParam(value = "currentCount", defaultValue = "0") long current, HttpServletResponse response) {

        long newLineCount = current + 1;
        long newTotalQty = (current + 1) * 2; // 그냥 수량은 2배라고 가정

        // [수정] 빌더 패턴으로 lineCount 설정
        CartSummaryResponseDto newSummary = CartSummaryResponseDto.builder()
                .lineCount(newLineCount)
                .totalQuantity(newTotalQty)
                .build();

        String signedCookie = cookieSerializer.encrypt(newSummary);

        ResponseCookie cookie = ResponseCookie.from("CART_BADGE", signedCookie)
                .path("/")
                .maxAge(Duration.ofMinutes(30))
                .httpOnly(false)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return "ok";
    }

    @PostMapping("/clear")
    @ResponseBody
    public String clearCart(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("CART_BADGE", "")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return "cleared";
    }
}