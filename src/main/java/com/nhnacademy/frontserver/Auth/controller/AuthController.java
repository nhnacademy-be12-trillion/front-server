package com.nhnacademy.frontserver.Auth.controller;

import com.nhnacademy.frontserver.Auth.adapter.MemberAdapter;
import com.nhnacademy.frontserver.Auth.dto.LoginRequest;
import com.nhnacademy.frontserver.Auth.dto.TokenResponse;
import com.nhnacademy.frontserver.Auth.util.CookieUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final MemberAdapter memberAdapter;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, HttpServletResponse response) {

        // FeignClient로 Gateway 호출 -> 토큰 받기
        TokenResponse tokens = memberAdapter.login(loginRequest);
        ResponseCookie accessCookie = CookieUtils.createHttpOnlyCookie("accessToken", tokens.getAccessToken(), 60 * 30);
        ResponseCookie refreshCookie = CookieUtils.createHttpOnlyCookie("refreshToken", tokens.getRefreshToken(), 60 * 60 * 24 * 7);

        // 응답 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        // Auth Service에 로그아웃 요청 (Redis Blacklist 등록)
        // FeignInterceptor가 현재 쿠키의 AccessToken을 헤더에 담아 보냄
        try {
            memberAdapter.logout();
        } catch (Exception e) {
        }
        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.deleteCookie("accessToken").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, CookieUtils.deleteCookie("refreshToken").toString());

        return "redirect:/";
    }
}