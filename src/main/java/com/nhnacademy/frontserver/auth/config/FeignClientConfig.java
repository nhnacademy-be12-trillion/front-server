package com.nhnacademy.frontserver.auth.config;

import com.nhnacademy.frontserver.common.Token;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FeignClientConfig {

    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 재발급 요청은 가로채지 않음
            if (template.path().contains("/reissue")) {
                return;
            }

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            // 현재 요청 컨텍스트가 존재할 때만 로직 수행
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                // Authorization 헤더 설정
                template.removeHeader(HttpHeaders.AUTHORIZATION);
                String accessToken = Token.getAccessToken(request);
                if (accessToken != null) {
                    template.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                }

                // guestId 쿠키 추출 및 전달
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("guestId".equals(cookie.getName())) {
                            template.header("Cookie", "guestId=" + cookie.getValue());
                            break;
                        }
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