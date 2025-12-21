package com.nhnacademy.frontserver.auth.client;

import com.nhnacademy.frontserver.auth.dto.LoginRequest;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "gateway-auth",
        url = "${gateway.url}",
        contextId = "gateway-auth")
public interface AuthClient {

    @PostMapping("/api/auth/login")
    TokenResponse login(@RequestBody LoginRequest loginRequest);

    @PostMapping("/api/auth/logout")
    void logout(@RequestHeader("Authorization") String accessToken);;

    @PostMapping("/api/auth/reissue")
    TokenResponse reissue(@RequestHeader("X-Refresh-Token") String refreshToken);

    // 토큰 삭제 (헤더로 Refresh Token 전달)
    @DeleteMapping("/api/auth/withdraw")
    void withdraw(@RequestHeader("X-Refresh-Token") String refreshToken);
}