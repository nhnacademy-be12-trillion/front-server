package com.nhnacademy.frontserver.auth.config;

import com.nhnacademy.frontserver.auth.util.TokenHolder;
import feign.RequestInterceptor;
import feign.Retryer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

            boolean headerSet = false;

            String newAccessToken = TokenHolder.get();
            if (newAccessToken != null) {
                log.info("Feign Interceptor: TokenHolder의 새 토큰 적용");
                template.header("Authorization", "Bearer " + newAccessToken);
                TokenHolder.clear();
                headerSet = true;
            }

            if (template.headers().containsKey("Authorization")) {
                headerSet = true;
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Cookie[] cookies = request.getCookies();

                if (cookies != null) {
                    StringBuilder cookieHeader = new StringBuilder();

                    for (Cookie cookie : cookies) {
                        if (cookieHeader.length() > 0) {
                            cookieHeader.append("; ");
                        }
                        cookieHeader.append(cookie.getName()).append("=").append(cookie.getValue());

                        if (!headerSet && "accessToken".equals(cookie.getName())) {
                            template.header("Authorization", "Bearer " + cookie.getValue());
                            headerSet = true;
                        }
                    }
                    template.header("Cookie", cookieHeader.toString());
                }
            }
        };
    }

    @Bean
    public Retryer retryer() {
        // 100ms 간격으로 최대 2회 재시도
        return new Retryer.Default(100L, 1000L, 2);
    }
}