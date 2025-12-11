package com.nhnacademy.frontserver.infra;

import feign.okhttp.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignOkHttpConfig {

    // PATCH 메서드를 지원하는 OkHttpClient 사용
    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }
}
