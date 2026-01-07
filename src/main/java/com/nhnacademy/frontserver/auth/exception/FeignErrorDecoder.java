package com.nhnacademy.frontserver.auth.exception;

import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import com.nhnacademy.frontserver.common.Token;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
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
        log.debug("methodKey: {}, response: {}",methodKey,response);
        if (methodKey.contains("login") || methodKey.contains("reissue")) {
            return defaultDecoder.decode(methodKey, response);
        }

        if (response.status() == HttpStatus.SC_UNAUTHORIZED) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                HttpServletRequest request = attributes.getRequest();
                HttpServletResponse servletResponse = attributes.getResponse();

                try {
                        if(Token.canReissue(request)) {
                            TokenResponse newTokens = authClient.reissue(Token.getRefreshToken(request));
                            Token.reissue(request,servletResponse,newTokens);

                            return new RetryableException(
                                    response.status(),
                                    "Token reissued",
                                    response.request().httpMethod(),
                                    (Long) null,
                                    response.request()
                            );
                        }
                    } catch (Exception e) {
                        return defaultDecoder.decode(methodKey, response);
                    }
                }
        return defaultDecoder.decode(methodKey, response);
    }
}