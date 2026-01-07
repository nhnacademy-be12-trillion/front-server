package com.nhnacademy.frontserver.auth.config;

import com.nhnacademy.frontserver.common.Token;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
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
            // 재발급 요청은 간섭하지 않음
            if (template.path().contains("/reissue")) {
                return;
            }
            template.removeHeader(HttpHeaders.AUTHORIZATION);
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                template.header(HttpHeaders.AUTHORIZATION, "Bearer " + Token.getAccessToken(request));
            }
        };
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(500L, 2000L, 2);
    }
}