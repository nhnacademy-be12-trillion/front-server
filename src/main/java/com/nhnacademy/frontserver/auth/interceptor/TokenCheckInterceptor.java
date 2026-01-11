package com.nhnacademy.frontserver.auth.interceptor;

import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import com.nhnacademy.frontserver.common.Token;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
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

    private void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if(Token.getAccessToken(request)==null&&Token.canReissue(request)){
            try{
                TokenResponse reissue = authClient.reissue(Token.getRefreshToken(request));
                Token.reissue(request,response,reissue);
            }catch (Exception ex){
                log.info("token reissue error:{}",Token.getRefreshToken(request));
                Token.removeToken(response);
                response.sendRedirect("/login");
            }
        }
    }

}
