package com.nhnacademy.frontserver.auth.exception;

import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import com.nhnacademy.frontserver.auth.util.CookieUtils;
import com.nhnacademy.frontserver.auth.util.TokenHolder;
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

    private final AuthClient authClient;
    private final ErrorDecoder defaultDecoder = new Default();

    public FeignErrorDecoder(@Lazy AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        if (methodKey.contains("login") || methodKey.contains("reissue")) {
            return defaultDecoder.decode(methodKey, response);
        }

        if (response.status() == 401) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                HttpServletResponse servletResponse = attributes.getResponse();
                String refreshToken = CookieUtils.getCookieValue(request, "refreshToken");

                if (refreshToken != null) {
                    try {
                        TokenResponse newTokens = authClient.reissue(refreshToken);

                        if (servletResponse != null) {
                            servletResponse.addHeader("Set-Cookie", CookieUtils.createHttpOnlyCookie("accessToken", newTokens.getAccessToken(), 1800).toString());
                            servletResponse.addHeader("Set-Cookie", CookieUtils.createHttpOnlyCookie("refreshToken", newTokens.getRefreshToken(), 60 * 60 * 24 * 7).toString());
                        }

                        TokenHolder.set(newTokens.getAccessToken());

                        return new RetryableException(
                                response.status(),
                                "Token reissued",
                                response.request().httpMethod(),
                                (Long) null,
                                response.request()
                        );

                    } catch (Exception e) {
                        return defaultDecoder.decode(methodKey, response);
                    }
                }
            }
        }

        return defaultDecoder.decode(methodKey, response);
    }
}