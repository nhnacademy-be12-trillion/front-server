package com.nhnacademy.frontserver.common.cartsummary;



import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CartBadgeInterceptor implements HandlerInterceptor {

    private final CartBadgeManager cartBadgeManager;

    private static final String COOKIE_NAME = "CART_BADGE";
    private static final Duration COOKIE_AGE = Duration.ofMinutes(60);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        // 1. 요청에서 쿠키 값 추출 (없으면 null)
        String rawCookie = Optional.ofNullable(WebUtils.getCookie(request, COOKIE_NAME))
                .map(jakarta.servlet.http.Cookie::getValue)
                .orElse(null);

        // 2. 매니저에게 판단 위임 (순수 자바 로직 호출)
        CartBadgeResult result = cartBadgeManager.resolveBadge(rawCookie);

        // 3. 매니저가 "쿠키 갱신해!" 라고 했으면 굽기 (Infrastructure)
        // 문제시 이부분 설정 주의
        if (result.needsCookieUpdate()) {
            ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, result.getCookieValueToSet())
                    .path("/")
                    .httpOnly(false) // JS에서 뱃지 DOM 조작을 위해 읽기 허용 시 false
                    .maxAge(COOKIE_AGE)
                    .sameSite("Strict")
                    //.secure(true) // HTTPS 환경 필수
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        // 4. View(Thymeleaf)에서 쓸 수 있게 Model 속성으로 주입
        // (Controller에 도달하기 전에 미리 넣어둠)
        request.setAttribute("cartSummary", result.getSummary());

        return true;
    }
}