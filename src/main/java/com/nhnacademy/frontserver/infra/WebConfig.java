package com.nhnacademy.frontserver.infra;


import com.nhnacademy.frontserver.common.cartsummary.CartBadgeInterceptor;
import com.nhnacademy.frontserver.infra.argumentResolver.CustomArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final List<CustomArgumentResolver> argumentResolvers;

    private final CartBadgeInterceptor cartBadgeInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(argumentResolvers);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(cartBadgeInterceptor)
                .addPathPatterns("/**") // 모든 페이지에 적용
                .excludePathPatterns( //TODO 추후 추가적으로 제외 패턴 추가
                        "/static/**", "/css/**", "/js/**", "/images/**", // 정적 자원 제외
                        "/favicon.ico", "/error", "/api/**" // API 요청 등 제외
                );
    }
}
