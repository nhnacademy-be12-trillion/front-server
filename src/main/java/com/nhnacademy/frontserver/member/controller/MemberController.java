package com.nhnacademy.frontserver.member.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.auth.client.AuthClient;
import com.nhnacademy.frontserver.auth.util.CookieUtils;
import com.nhnacademy.frontserver.member.*;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.client.OrderClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberClient memberClient;
    private final AuthClient authClient;
    private final OrderClient orderClient;

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signupRequest", new MemberSignupRequest(
                null, null, null, null, null, null,
                new AddressCreateRequest(null, null, null, "우리집")
        ));
        return "signup";
    }

    // 회원가입 처리 (Form Submit)
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupRequest") MemberSignupRequest request,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup";
        }
        try {
            memberClient.signup(request);
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Signup failed", e);
            bindingResult.reject("signupFail", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return "signup";
        }
    }

    // 비밀번호 재설정 페이지 이동
    @GetMapping("/password/reset")
    public String findPasswordForm() {
        return "find-password";
    }

    // 비밀번호 변경 처리 (Form Submit)
    @PostMapping("/password/reset")
    public String resetPassword(@ModelAttribute PasswordResetRequest request, RedirectAttributes redirectAttributes) {
        try {
            memberClient.resetPassword(request);
            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다. 로그인해주세요.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "변경 실패: 인증번호가 틀렸거나 만료되었습니다.");
            return "redirect:/members/password/reset";
        }
    }

    // 마이페이지
    @GetMapping("/my-page")
    public String myPage(Model model, HttpServletResponse response) {
        try {
            MemberResponse member = memberClient.getMember();
            model.addAttribute("member", member);

            List<AddressResponse> addresses = memberClient.getAllAddresses();
            model.addAttribute("addresses", addresses);

            model.addAttribute("addressRequest", new AddressCreateRequest(null, null, null, null));

            return "my/my-page";

        } catch (Exception e) {
            log.warn("마이페이지 접근 실패: {}", e.getMessage());
            forceLogout(response);
            return "redirect:/login?error=access_denied";
        }
    }

    // 이메일 찾기 페이지
    @GetMapping("/find/email")
    public String findEmailForm() {
        return "find-email";
    }

    // 이메일 찾기 처리 (Form Submit -> 결과 페이지)
    @PostMapping("/find/email")
    public String findEmail(@ModelAttribute FindMemberIdRequest request, Model model) {
        try {
            String email = memberClient.findEmail(request);
            model.addAttribute("foundEmail", email);
        } catch (Exception e) {
            model.addAttribute("error", "일치하는 회원 정보를 찾을 수 없습니다.");
        }
        return "find-email";
    }

    // 주소 추가 (Form Submit)
    @PostMapping("/addresses")
    public String addAddress(@ModelAttribute AddressCreateRequest request) {
        memberClient.addAddress(request);
        return "redirect:/members/my-page";
    }

    // 주소 삭제 (Form Submit)
    @PostMapping("/addresses/{addressId}/delete")
    public String deleteAddress(@PathVariable Long addressId) {
        memberClient.deleteAddress(addressId);
        return "redirect:/members/my-page";
    }

    // 주소 수정 (Form Submit)
    @PostMapping("/addresses/{addressId}/update")
    public String updateAddress(@PathVariable Long addressId, @ModelAttribute AddressUpdateRequest request) {
        memberClient.updateAddress(addressId, request);
        return "redirect:/members/my-page";
    }

    // 회원 탈퇴 (Form Submit)
    @PostMapping("/withdraw")
    public String withdrawMember(HttpServletRequest request, HttpServletResponse response) {
        try {
            memberClient.withdrawMember();
        } catch (Exception e) {
            log.error("Member Service 탈퇴 처리 실패", e);
        }

        String refreshToken = CookieUtils.getCookieValue(request, "refreshToken");
        if (refreshToken != null) {
            try {
                authClient.withdraw(refreshToken);
            } catch (Exception e) {
                log.warn("Auth Service 토큰 삭제 실패 : {}", e.getMessage());
            }
        }

        forceLogout(response);
        return "redirect:/";
    }

    // 소셜 회원가입 추가 정보 입력 페이지
    @GetMapping("/social-signup")
    public String socialSignupForm() {
        return "social-signup";
    }

    // 소셜 추가 정보 저장 처리
    @PostMapping("/social-signup")
    public String submitSocialInfo(@ModelAttribute SocialInfoUpdateRequest request, // DTO 이름 확인 필요
                                   HttpServletResponse response) {
        memberClient.updateSocialMember(request);
        forceLogout(response); // 정보 갱신 후 재로그인 유도
        return "redirect:/login?social_complete=true";
    }

    // 등급 조회
    @GetMapping("/grades")
    public String gradeForm(Model model) {
        List<GradeResponse> grades = memberClient.getGrades();
        model.addAttribute("grades", grades);
        return "grade-list";
    }

    // 주문 내역 조회
    @GetMapping("/orders")
    public String myPageOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        String sort = "bookId,desc";
        PageResponse<OrderResponse> orders = orderClient.getAllOrderByMember(page, size, sort);
        model.addAttribute("orders", orders);
        model.addAttribute("activeTab", "orders");
        return "/my/my-orders";
    }

    // 리뷰 조회
    @GetMapping("/reviews")
    public String getReviews() {
        return "/my/my-reviews";
    }

    private void forceLogout(HttpServletResponse response) {
        ResponseCookie accessCookie = CookieUtils.deleteCookie("accessToken");
        ResponseCookie refreshCookie = CookieUtils.deleteCookie("refreshToken");

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}