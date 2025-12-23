package com.nhnacademy.frontserver.auth.exception;

import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import com.nhnacademy.frontserver.auth.util.CookieUtils;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();
    private final AuthClient authClient;

    public FeignErrorDecoder(@Lazy AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        // 로그인이나 재발급 요청 자체가 실패한 경우 -> 재시도 금지 (루프 방지)
        if (methodKey.contains("login") || methodKey.contains("reissue")) {
            return defaultDecoder.decode(methodKey, response);
        }

        if (response.status() == 401) {
            log.info("Access Token 만료 감지. 재발급 시도 중... (Method: {})", methodKey);

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpServletResponse servletResponse = attributes.getResponse();

                String refreshToken = CookieUtils.getCookieValue(request, "refreshToken");

                if (refreshToken != null) {
                    try {
                        // 재발급 요청 (Interceptor가 /reissue 경로를 보고 Authorization 헤더 생략)
                        TokenResponse newTokens = authClient.reissue(refreshToken);

                        // 쿠키 갱신
                        if (servletResponse != null) {
                            servletResponse.addHeader("Set-Cookie",
                                    CookieUtils.createHttpOnlyCookie("accessToken", newTokens.getAccessToken(), 1800).toString());
                            // Refresh Token Rotation이 있다면 이것도 갱신
                            servletResponse.addHeader("Set-Cookie",
                                    CookieUtils.createHttpOnlyCookie("refreshToken", newTokens.getRefreshToken(), 60 * 60 * 24 * 7).toString());
                        }

                        log.info("토큰 재발급 성공! 원래 요청을 다시 시도합니다.");

                        feign.Request originalRequest = response.request();
                        java.util.Map<String, java.util.Collection<String>> headers = new java.util.HashMap<>(originalRequest.headers());
                        headers.put("Authorization", java.util.Collections.singletonList("Bearer " + newTokens.getAccessToken()));

                        feign.Request newRequest = feign.Request.create(
                                originalRequest.httpMethod(),
                                originalRequest.url(),
                                headers,
                                originalRequest.body(),
                                originalRequest.charset() != null ? originalRequest.charset() : java.nio.charset.StandardCharsets.UTF_8
                        );

                        return new RetryableException(
                                response.status(),
                                "Token reissued",
                                response.request().httpMethod(),
                                (Long) null,
                                newRequest
                        );

                    } catch (Exception e) {
                        log.error("재발급 실패 (로그아웃 필요): {}", e.getMessage());
                        // 재발급 실패 시 401 에러 그대로 전달 -> 로그인 페이지로 튕기게 유도
                        return defaultDecoder.decode(methodKey, response);
                    }
                }
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}