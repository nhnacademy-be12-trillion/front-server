package com.nhnacademy.frontserver.auth.config;

import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class FeignClientConfig {

    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 재발급 요청은 간섭하지 않음
            if (template.path().contains("/reissue")) {
                return;
            }

            // 쿠키 헤더 재조립 (가장 중요)
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    List<String> cookieList = new ArrayList<>();
                    for (Cookie cookie : cookies) {
                        cookieList.add(cookie.getName() + "=" + cookie.getValue());
                        // 재발급된 상태이고, 현재 쿠키가 accessToken이라면
                        if ("accessToken".equals(cookie.getName())) {
                            template.header("Authorization", "Bearer " + cookie.getValue());
                        }
                    }
                    // 기존 Feign이 자동으로 붙였을 수도 있는 Cookie 헤더를 밀어버리고 새로 만든 문자열로 덮어씌움
                    String newCookieHeader = String.join("; ", cookieList);
                    template.header("Cookie", newCookieHeader);
                }
            }
        };
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(500L, 2000L, 2);
    }
}