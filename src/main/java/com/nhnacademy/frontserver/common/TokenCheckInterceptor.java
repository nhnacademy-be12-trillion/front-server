package com.nhnacademy.frontserver.common;

import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TokenCheckInterceptor implements HandlerInterceptor {

    private final AuthClient authClient;

    public TokenCheckInterceptor(@Lazy AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        reissueAccessToken(request,response);
        return true;
    }

    private void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        if(Token.getAccessToken(request)==null&&Token.canReissue(request)){
            TokenResponse reissue = authClient.reissue(Token.getRefreshToken(request));
            Token.reissue(request,response,reissue);
        }
    }

}
