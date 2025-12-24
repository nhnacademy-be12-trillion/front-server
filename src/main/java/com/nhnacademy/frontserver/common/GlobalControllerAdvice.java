package com.nhnacademy.frontserver.common;

import com.fasterxml.jackson.databind.ObjectMapper; // 추가
import com.nhnacademy.frontserver.auth.util.CookieUtils;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Base64; // 추가
import java.util.Map;    // 추가

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MemberClient memberClient;
    private final ObjectMapper objectMapper; // JSON 파싱을 위해 Spring이 주입해줌

    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest request, Model model) {
        if ("/error".equals(request.getRequestURI())) {
            return;
        }

        // 1. 쿠키에서 토큰 가져오기
        String accessToken = CookieUtils.getCookieValue(request, "accessToken");

        if (accessToken != null) {
            try {
                // 2. 회원 정보 조회 (이름 등을 위해)
                MemberResponse member = memberClient.getMember();
                model.addAttribute("isLogin", true);
                model.addAttribute("member", member);

                // 3. [추가] 토큰을 직접 열어서 관리자(ADMIN)인지 확인
                boolean isAdmin = checkAdminRole(accessToken);
                model.addAttribute("isAdmin", isAdmin); // 화면(header.html)에 알려줌

            } catch (Exception e) {
                log.error("로그인 정보 조회 실패: {}", e.getMessage());
                model.addAttribute("isLogin", false);
                model.addAttribute("isAdmin", false);
            }
        } else {
            model.addAttribute("isLogin", false);
            model.addAttribute("isAdmin", false);
        }
    }

    /**
     * JWT 토큰의 Payload를 디코딩하여 role이 'ADMIN'인지 확인하는 메서드
     */
    private boolean checkAdminRole(String accessToken) {
        try {
            // JWT는 점(.)으로 3부분이 나뉩니다. (Header.Payload.Signature)
            String[] chunks = accessToken.split("\\.");
            if (chunks.length < 2) return false;

            // 두 번째 부분(Payload)을 Base64 디코딩
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            // JSON 문자열을 Map으로 변환 -> {"role": "ADMIN", ...}
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);

            // role 값 확인
            String role = (String) claims.get("role");
            return "ADMIN".equals(role);

        } catch (Exception e) {
            log.warn("토큰 파싱 중 오류 발생: {}", e.getMessage());
            return false;
        }
    }
}