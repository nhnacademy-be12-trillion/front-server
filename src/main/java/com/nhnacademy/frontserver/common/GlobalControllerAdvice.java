package com.nhnacademy.frontserver.common;

import com.nhnacademy.frontserver.auth.util.CookieUtils;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MemberClient memberClient;

    /*
     * 모든 컨트롤러(View 반환) 요청 시 실행
     * 쿠키에 accessToken이 있으면 MemberClient를 통해 내 정보를 조회하고 Model에 담음
     */
    @ModelAttribute
    public void addCommonAttributes(HttpServletRequest request, Model model) {
        if ("/error".equals(request.getRequestURI())) {
            return;
        }

        String accessToken = CookieUtils.getCookieValue(request, "accessToken");

        if (accessToken != null) {
            try {
                MemberResponse member = memberClient.getMember();
                model.addAttribute("isLogin", true);
                model.addAttribute("member", member);
            } catch (Exception e) {
                log.error("Unexpected System Error 발생! URL={}, Message={}", request.getRequestURI(), e.getMessage(), e);
                model.addAttribute("isLogin", false);
            }
        } else {
            model.addAttribute("isLogin", false);
        }
    }
}