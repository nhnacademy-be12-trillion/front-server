package com.nhnacademy.frontserver.auth.config;

import com.nhnacademy.frontserver.auth.util.TokenHolder;
import feign.RequestInterceptor;
import feign.Retryer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            if ("/api/auth/reissue".equals(template.path())) {
                return;
            }

            boolean useTokenHolder = false;

            // 재발급된 토큰이 있는지 확인 (재시도 로직)
            String newAccessToken = TokenHolder.get();
            if (StringUtils.hasText(newAccessToken)) {
                log.info("Feign Interceptor: TokenHolder의 새 토큰으로 요청 재시도");
                template.header("Authorization", "Bearer " + newAccessToken);
                TokenHolder.clear();
                useTokenHolder = true;
            }

            // 이미 헤더가 있으면 패스
            if (template.headers().containsKey("Authorization")) {
                return;
            }

            // 쿠키에서 토큰 추출 (TokenHolder를 사용하지 않은 경우에만)
            if (!useTokenHolder) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    Cookie[] cookies = request.getCookies();

                    if (cookies != null) {
                        StringBuilder cookieHeader = new StringBuilder();
                        String accessTokenFromCookie = null;

                        for (Cookie cookie : cookies) {
                            log.info("cookie조회 :{},{}",cookie.getName(),cookie.getValue());
                        }

                        for (Cookie cookie : cookies) {
                            if (cookieHeader.length() > 0) {
                                cookieHeader.append("; ");
                            }
                            cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue());

                            // accessToken 쿠키를 찾아서 값을 꺼내는 로직
                            if ("accessToken".equals(cookie.getName())) {
                                accessTokenFromCookie = cookie.getValue();
                            }
                        }
                        log.info("cookieAccessToken:{}",accessTokenFromCookie);

                        // AccessToken이 있으면 헤더에 추가
                        if (accessTokenFromCookie != null) {
                            log.debug("Feign Interceptor: 쿠키 기반 토큰 설정 완료");
                            template.header("Authorization", "Bearer " + accessTokenFromCookie);
                        } else {
                            // 디버깅용 로그: 쿠키는 있는데 accessToken만 없는 경우
                            log.warn("Feign Interceptor: 쿠키 목록은 존재하나 accessToken을 찾지 못함");
                        }

                        // 토큰 재발급 상황(재시도)이 아닐 때만 쿠키를 실어 보냄
                        template.header("Cookie", cookieHeader.toString());
                    } else {
                        log.warn("Feign Interceptor: 요청에 쿠키가 없음");
                    }
                }
            }
        };
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(500L, 2000L, 2);
    }
}