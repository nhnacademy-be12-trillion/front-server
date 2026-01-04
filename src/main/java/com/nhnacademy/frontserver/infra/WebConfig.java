package com.nhnacademy.frontserver.infra;

import com.nhnacademy.frontserver.CheckTimeInterceptor;
import com.nhnacademy.frontserver.auth.interceptor.GuestCookieInterceptor;
import com.nhnacademy.frontserver.auth.util.TokenHolder;
import com.nhnacademy.frontserver.common.AdminCheckInterceptor;
import com.nhnacademy.frontserver.infra.argumentResolver.CustomArgumentResolver;
import com.nhnacademy.frontserver.layout.interceptor.LayoutDataInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final List<CustomArgumentResolver> argumentResolvers;

    private final LayoutDataInterceptor layoutDataInterceptor;
    private final GuestCookieInterceptor guestCookieInterceptor;
    private final CheckTimeInterceptor checkTimeInterceptor;

    // [추가됨] 관리자 권한 체크 인터셉터 주입
    private final AdminCheckInterceptor adminCheckInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(argumentResolvers);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 1. 레이아웃 데이터 설정
        registry.addInterceptor(layoutDataInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/static/**", "/css/**", "/js/**", "/images/**",
                        "/favicon.ico", "/error", "/api/**"
                );

        // 2. 비회원 쿠키 설정
        registry.addInterceptor(guestCookieInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/static/**", "/css/**", "/js/**", "/images/**",
                        "/favicon.ico", "/error", "/api/**"
                );

        // 3. 실행 시간 체크
        registry.addInterceptor(checkTimeInterceptor);

        // 4. [추가됨] 관리자 페이지 접근 보안 설정
        // /admin/** 경로로 들어오는 모든 요청에 대해 AdminCheckInterceptor를 실행합니다.
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**", "/api/admin/**") // 검사할 경로
                .excludePathPatterns( // 검사 제외 경로 (정적 자원 등)
                        "/admin/login",
                        "/css/**", "/js/**", "/img/**", "/lib/**",
                        "/favicon.ico", "/error"
                );
        registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                        TokenHolder.clear();
                    }
                })
                .addPathPatterns("/**");
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }
}