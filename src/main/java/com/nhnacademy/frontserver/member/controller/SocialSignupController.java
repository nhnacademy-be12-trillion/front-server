package com.nhnacademy.frontserver.member.controller;

import com.nhnacademy.frontserver.auth.util.CookieUtils;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.MemberResponse;
import com.nhnacademy.frontserver.member.SocialSignupRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members/social-signup")
@RequiredArgsConstructor
public class SocialSignupController {

    private final MemberClient memberClient;

    @GetMapping
    public String socialSignupForm(@RequestParam String accessToken,
                                   @RequestParam String refreshToken,
                                   @RequestParam("memberOauthId") String memberOauthId,
                                   Model model,
                                   HttpServletResponse response) {

        ResponseCookie accessCookie = CookieUtils.createHttpOnlyCookie("accessToken", accessToken, 60 * 30);
        ResponseCookie refreshCookie = CookieUtils.createHttpOnlyCookie("refreshToken", refreshToken, 60 * 60 * 24 * 7);
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        MemberResponse memberInfo = memberClient.getMemberByOauthId(memberOauthId);

        // 받아온 정보로 폼 초기화
        SocialSignupRequest request = new SocialSignupRequest(
                memberInfo.memberEmail(),
                memberInfo.memberName(),
                null, null,
                memberInfo.memberOauthId(),
                null
        );


        model.addAttribute("socialSignupRequest", request);
        return "social-signup";
    }

    @PostMapping
    public String processSocialSignup(@ModelAttribute SocialSignupRequest request, Model model) {
        try {
            memberClient.updateSocialMember(request);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("socialSignupRequest", request);
            return "social-signup";
        }
    }
}