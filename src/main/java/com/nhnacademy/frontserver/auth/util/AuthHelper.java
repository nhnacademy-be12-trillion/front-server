package com.nhnacademy.frontserver.auth.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.common.Token;
import com.nhnacademy.frontserver.member.MemberResponse;
import com.nhnacademy.frontserver.member.client.MemberClient;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component("authHelper") // 타임리프에서 @authHelper로 접근
@RequiredArgsConstructor
public class AuthHelper {

    private final ObjectMapper objectMapper;
    private final MemberClient memberClient;

    // 현재 로그인한 회원 정보 가져오기 (뷰에서 직접 호출)
    public MemberResponse getMember() {
        if (!isLogin()) {
            return null;
        }
        try {
            // FeignClient가 TokenHolder의 최신 토큰을 써서 가져옴
            return memberClient.getMember();
        } catch (Exception e) {
            log.warn("AuthHelper: 회원 정보 조회 실패 - {}", e.getMessage());
            return null;
        }
    }

    public boolean isLogin() {
        return getToken()!=null;
    }

    public boolean isAdmin() {
        String token = getToken();
        if (!StringUtils.hasText(token)) {
            return false;
        }
        return checkAdminRole(token);
    }

    // 현재 유효한 토큰 가져오기 (재발급된 것 우선)
    private String getToken() {
        // 방금 재발급된 토큰 확인
        // 없으면 쿠키에서 확인
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr != null) {
            HttpServletRequest request = attr.getRequest();
            return Token.getAccessToken(request);
        }
        return null;
    }

    // 토큰 파싱해서 ROLE 확인
    private boolean checkAdminRole(String accessToken) {
        try {
            String[] chunks = accessToken.split("\\.");
            if (chunks.length < 2) return false;

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);

            String role = (String) claims.get("role");
            return "ADMIN".equals(role);
        } catch (Exception e) {
            return false;
        }
    }
}