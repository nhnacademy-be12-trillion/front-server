package com.nhnacademy.frontserver.infra;


import com.nhnacademy.frontserver.infra.argumentResolver.CustomArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final List<CustomArgumentResolver> argumentResolvers;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(argumentResolvers);
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
