package com.nhnacademy.frontserver.auth.controller;

import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.dto.LoginRequest;
import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import com.nhnacademy.frontserver.auth.util.CookieUtils;
import com.nhnacademy.frontserver.cart.client.CartClient;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthClient authClient;

    private final CartClient cartClient;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @CookieValue(name = "guestId", required = false) String guestId,
            @ModelAttribute LoginRequest loginRequest,
                        HttpServletResponse response) {
        try {
            // FeignClient로 Gateway 호출 -> 토큰 받기
            TokenResponse tokens = authClient.login(loginRequest);
            ResponseCookie accessCookie = CookieUtils.createHttpOnlyCookie("accessToken", tokens.getAccessToken(), 60 * 30);
            ResponseCookie refreshCookie = CookieUtils.createHttpOnlyCookie("refreshToken", tokens.getRefreshToken(), 60 * 60 * 24 * 7);

            // 응답 헤더에 추가
            response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            return "redirect:/carts/merge";
        }catch (FeignException.Forbidden e){
            String responseBody = e.contentUTF8();
            if (responseBody.contains("DORMANT")) {
                log.info("휴면 계정 접속 시도: {}", loginRequest.memberEmail());
                return "redirect:/members/dormant"; // 휴면 해제 페이지로 이동
            } else if (responseBody.contains("WITHDRAWAL")) {
                log.info("탈퇴 계정 접속 시도: {}", loginRequest.memberEmail());
                return "redirect:/login?error=withdrawal"; // 탈퇴한 회원 메시지 처리
            }
            return "redirect:/login?error"; // 그 외 권한 없음
        }catch (FeignException e){
            log.info("로그인 실패: {}", e.getMessage());
            return "redirect:/login?error";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Auth Service에 로그아웃 요청 (Redis Blacklist 등록)
        // FeignInterceptor가 현재 쿠키의 AccessToken을 헤더에 담아 보냄
        try {
            authClient.logout();
        } catch (Exception e) {
            log.warn("로그아웃 처리 중 오류 (무시됨): {}", e.getMessage());
        }
        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.deleteCookie("accessToken").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.deleteCookie("refreshToken").toString());

        return "redirect:/";
    }

    @GetMapping("/login/oauth2/success")
    public String socialLoginSuccess(@RequestParam String accessToken,
                                     @RequestParam String refreshToken,
                                     HttpServletResponse response) {
        // 쿠키 생성
        ResponseCookie accessCookie = CookieUtils.createHttpOnlyCookie("accessToken", accessToken, 60 * 30);
        ResponseCookie refreshCookie = CookieUtils.createHttpOnlyCookie("refreshToken", refreshToken, 60 * 60 * 24 * 7);
        // 응답에 쿠키 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return "redirect:/";
    }
}