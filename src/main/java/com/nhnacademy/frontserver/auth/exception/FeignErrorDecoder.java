package com.nhnacademy.frontserver.auth.exception;

import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import com.nhnacademy.frontserver.auth.util.CookieUtils;
import feign.Response;
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
    private final AuthClient authClient; // 재발급 요청용

    // 순환 참조(Bean Cycle) 방지를 위해 @Lazy 사용
    public FeignErrorDecoder(@Lazy AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        // 401 에러가 발생했는지 확인
        if (response.status() == 401) {
            log.info("Access Token Expired: Trying to reissue...");

            // 현재 요청의 컨텍스트(Request/Response) 가져오기
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpServletResponse servletResponse = attributes.getResponse();

                // Refresh Token 쿠키 찾기
                String refreshToken = CookieUtils.getCookieValue(request, "refreshToken");

                if (refreshToken != null) {
                    try {
                        // Auth 서비스에 재발급 요청 (헤더로 Refresh Token 전송)
                        // 주의: MemberAdapter에 reissue 메소드 구현 필요
                        TokenResponse newTokens = authClient.reissue(refreshToken);

                        // 성공 시 새 쿠키 굽기
                        if (servletResponse != null) {
                            servletResponse.addHeader("Set-Cookie",
                                    CookieUtils.createHttpOnlyCookie("accessToken", newTokens.getAccessToken(), 1800).toString());
                            // Refresh Token도 갱신된다면 같이 굽기
                        }

                        log.info("Token Reissue Success. Please retry the request.");
                        // todo 재발급 실패 시 예외처리
                        return new RuntimeException();
                    } catch (Exception e) {
                        log.error("Reissue Failed: {}", e.getMessage());
                        // 재발급 실패 시 로그아웃 처리 등을 위해 401 그대로 전달
                    }
                }
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}