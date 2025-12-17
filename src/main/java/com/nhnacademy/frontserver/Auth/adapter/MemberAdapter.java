package com.nhnacademy.frontserver.Auth.adapter;

import com.nhnacademy.frontserver.Auth.config.FeignClientConfig;
import com.nhnacademy.frontserver.Auth.dto.LoginRequest;
import com.nhnacademy.frontserver.Auth.dto.TokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "gateway-service", configuration = FeignClientConfig.class)
public interface MemberAdapter {

    @PostMapping("/auth/login")
    TokenResponse login(@RequestBody LoginRequest loginRequest);

    @PostMapping("/auth/logout")
    void logout();

    @PostMapping("/auth/reissue")
    TokenResponse reissue(@RequestHeader("X-Refresh-Token") String refreshToken);

    // FeignConfig 덕분에 X-Member-Id 헤더는 게이트웨이가 넣어줌 (혹은 여기서 직접 넣어도 됨)
    // 하지만 Member Service는 내부망 호출이므로 Gateway를 거친다면
    // Front -> Gateway(Auth검증) -> Member Service 순으로 가게 됨.
    @PostMapping("/api/members/withdraw")
    void withdraw();
}