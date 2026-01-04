package com.nhnacademy.frontserver.member.controller;

import com.nhnacademy.frontserver.member.DormantCodeRequest;
import com.nhnacademy.frontserver.member.DormantVerifyRequest;
import com.nhnacademy.frontserver.member.client.MemberClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/members/dormant")
@RequiredArgsConstructor
public class DormantController {

    private final MemberClient memberClient;

    @GetMapping
    public String dormantPage(Model model) {
        model.addAttribute("step", "request");
        return "dormant";
    }

    // 이메일 + 두레이 훅 URL 입력 -> 인증번호 발송 요청
    @PostMapping("/request")
    public String requestAuthCode(@RequestParam String memberEmail,
                                  @RequestParam String doorayHookUrl,
                                  Model model) {
        try {
            memberClient.requestDormantCode(new DormantCodeRequest(memberEmail, doorayHookUrl));
            model.addAttribute("message", "입력하신 두레이 훅으로 인증번호가 발송되었습니다.");
            model.addAttribute("memberEmail", memberEmail);
            model.addAttribute("step", "verify"); // 뷰에서 화면 전환용
            return "dormant";
        } catch (Exception e) {
            model.addAttribute("error", "발송 실패. 이메일과 훅 URL을 확인해주세요: " + e.getMessage());
            model.addAttribute("step", "request");
            return "dormant";
        }
    }

    // 인증번호 검증
    @PostMapping("/verify")
    public String verifyAuthCode(@RequestParam String memberEmail,
                                 @RequestParam String verificationCode,
                                 Model model) {
        try {
            memberClient.verifyDormantCode(new DormantVerifyRequest(memberEmail, verificationCode));
            return "redirect:/login?message=dormant_released";
        } catch (Exception e) {
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            model.addAttribute("step", "verify");
            model.addAttribute("memberEmail", memberEmail);
            return "dormant";
        }
    }
}