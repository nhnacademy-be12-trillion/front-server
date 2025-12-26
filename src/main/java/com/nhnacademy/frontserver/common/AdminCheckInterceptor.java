package com.nhnacademy.frontserver.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.auth.util.CookieUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class AdminCheckInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper; // 토큰 파싱용

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 쿠키에서 accessToken 꺼내기
        String accessToken = CookieUtils.getCookieValue(request, "accessToken");

        // 2. 토큰이 없으면 (비로그인) -> 로그인 페이지로 이동
        if (accessToken == null) {
            log.warn("미인증 사용자 관리자 페이지 접근 차단: {}", request.getRequestURI());
            response.sendRedirect("/login");
            return false; // 컨트롤러 실행 중단
        }

        // 3. 토큰이 있으면 권한 확인
        try {
            if (!isAdmin(accessToken)) {
                log.warn("권한 없는 사용자(일반 회원) 관리자 페이지 접근 차단: {}", request.getRequestURI());

                // 경고창을 띄우고 메인으로 보내거나, 그냥 리다이렉트
                response.setContentType("text/html; charset=UTF-8");
                response.getWriter().println("<script>alert('관리자 권한이 필요합니다.'); location.href='/';</script>");
                response.getWriter().flush();

                return false;
            }
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생", e);
            response.sendRedirect("/login");
            return false;
        }

        // 4. 관리자면 통과 (true 반환)
        return true;
    }

    // 토큰 파싱해서 role이 ADMIN인지 확인하는 메서드
    private boolean isAdmin(String accessToken) throws Exception {
        String[] chunks = accessToken.split("\\.");
        if (chunks.length < 2) return false;

        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        // JSON 파싱
        Map<String, Object> claims = objectMapper.readValue(payload, Map.class);
        String role = (String) claims.get("role");

        return "ADMIN".equals(role);
    }
}
