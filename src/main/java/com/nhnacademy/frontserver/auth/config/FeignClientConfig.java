package com.nhnacademy.frontserver.auth.config;

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
    public Retryer retryer() {
        return new Retryer.Default(100L, 1000L, 3);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            if ("/api/auth/reissue".equals(template.path())) {
                return;
            }
            if (template.headers().containsKey("Authorization")) {
                return;
            }
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Cookie[] cookies = request.getCookies();

                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("accessToken".equals(cookie.getName())) {
                            template.header("Authorization", "Bearer " + cookie.getValue());
                        }
                        if ("guestId".equals(cookie.getName())) {
                            template.header("Cookie", "guestId=" + cookie.getValue());
                        }
                    }
                }
            }
        };
    }
}