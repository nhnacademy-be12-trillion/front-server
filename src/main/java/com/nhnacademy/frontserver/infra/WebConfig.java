package com.nhnacademy.frontserver.infra;


import com.nhnacademy.frontserver.Auth.interceptor.GuestCookieInterceptor;
import com.nhnacademy.frontserver.CheckTimeInterceptor;
import com.nhnacademy.frontserver.infra.argumentResolver.CustomArgumentResolver;
import java.util.List;

import com.nhnacademy.frontserver.layout.interceptor.LayoutDataInterceptor;
import lombok.RequiredArgsConstructor;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final List<CustomArgumentResolver> argumentResolvers;

    private final LayoutDataInterceptor layoutDataInterceptor;
    private final GuestCookieInterceptor guestCookieInterceptor;
    private final CheckTimeInterceptor checkTimeInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(argumentResolvers);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(layoutDataInterceptor)
                .addPathPatterns("/**") // 모든 페이지에 적용
                .excludePathPatterns( //TODO 추후 추가적으로 제외 패턴 추가
                        "/static/**", "/css/**", "/js/**", "/images/**", // 정적 자원 제외
                        "/favicon.ico", "/error", "/api/**" // API 요청 등 제외
                );
        registry.addInterceptor(guestCookieInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns( //TODO 추후 추가적으로 제외 패턴 추가
                        "/static/**", "/css/**", "/js/**", "/images/**",
                        "/favicon.ico", "/error", "/api/**"
                );
        registry.addInterceptor(checkTimeInterceptor);
    }
    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}
