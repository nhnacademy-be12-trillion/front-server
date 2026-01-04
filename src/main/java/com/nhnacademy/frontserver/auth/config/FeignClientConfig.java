package com.nhnacademy.frontserver.auth.config;

import com.nhnacademy.frontserver.auth.util.TokenHolder;
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
import org.springframework.util.StringUtils;
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

            // TokenHolder에서 최신 토큰 확인 (재발급된 경우 여기에 값이 있음)
            String newAccessToken = TokenHolder.get();
            boolean isReissued = StringUtils.hasText(newAccessToken);

            // Authorization 헤더 처리
            if (isReissued) {
                // 기존 헤더 제거 후 새 토큰 주입
                template.header("Authorization", "Bearer " + newAccessToken);
                log.debug("Authorization 헤더 교체 완료 (새 토큰 적용)");
            }

            // 쿠키 헤더 재조립 (가장 중요)
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Cookie[] cookies = request.getCookies();

                if (cookies != null) {
                    List<String> cookieList = new ArrayList<>();
                    boolean accessTokenReplaced = false;

                    for (Cookie cookie : cookies) {
                        // 재발급된 상태이고, 현재 쿠키가 accessToken이라면
                        if (isReissued && "accessToken".equals(cookie.getName())) {
                            // 옛날 쿠키 값 대신 TokenHolder의 새 값을 넣음
                            cookieList.add(cookie.getName() + "=" + newAccessToken);
                            accessTokenReplaced = true;
                        } else {
                            cookieList.add(cookie.getName() + "=" + cookie.getValue());
                        }
                    }

                    // 만약 쿠키에 accessToken이 없었는데 재발급된 경우
                    if (isReissued && !accessTokenReplaced) {
                        cookieList.add("accessToken=" + newAccessToken);
                    }

                    // 기존 Feign이 자동으로 붙였을 수도 있는 Cookie 헤더를 밀어버리고 새로 만든 문자열로 덮어씌움
                    String newCookieHeader = String.join("; ", cookieList);
                    template.header("Cookie", newCookieHeader);

                    if (isReissued) {
                        log.debug("Cookie 헤더 내 accessToken 교체 완료");
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