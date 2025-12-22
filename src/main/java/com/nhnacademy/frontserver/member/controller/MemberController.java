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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

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

    // 회원가입 처리
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

    // 인증번호 발송 요청
    @ResponseBody
    @PostMapping("/emails/password")
    public ResponseEntity<String> sendResetEmail(@RequestBody Map<String, String> body) {
        String email = body.get("memberEmail");
        try {
            // EmailRequest DTO로 감싸서 전송
            memberClient.sendResetPasswordEmail(new EmailRequest(email));
            return ResponseEntity.ok("인증번호가 발송되었습니다.");
        } catch (Exception e) {
            // 백엔드에서 '가입되지 않은 이메일' 에러를 던지면 여기서 잡힘.
            return ResponseEntity.badRequest().body("가입되지 않은 이메일이거나 오류가 발생했습니다.");
        }
    }

    // 비밀번호 변경 요청
    @PostMapping("/password/reset")
    public String resetPassword(@ModelAttribute PasswordResetRequest request, RedirectAttributes redirectAttributes) {
        try {
            // 여기서 (이메일 + 인증코드 + 새비번)을 한꺼번에 보냄
            memberClient.resetPassword(request);

            redirectAttributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다. 로그인해주세요.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            // 인증코드가 틀렸거나 만료된 경우 여기서 예외 발생
            redirectAttributes.addFlashAttribute("error", "변경 실패: 인증번호가 틀렸거나 만료되었습니다.");
            return "redirect:/members/password/reset";
        }
    }

    // 마이페이지
    @GetMapping("/my-page")
    public String myPage(Model model, HttpServletResponse response) {
        try {
            // 회원 정보 조회 시도
            MemberResponse member = memberClient.getMember();
            model.addAttribute("member", member);

            List<AddressResponse> addresses = memberClient.getAllAddresses();
            model.addAttribute("addresses", addresses);

            model.addAttribute("addressRequest", new AddressCreateRequest(null, null, null, null));

            return "my/my-page";

        } catch (Exception e) {
            log.warn("마이페이지 접근 실패 (유효하지 않은 토큰 또는 탈퇴 회원): {}", e.getMessage());
            forceLogout(response);
            return "redirect:/auth/login?error=access_denied";
        }
    }

    // 이메일 찾기 페이지
    @GetMapping("/find/email")
    public String findEmailForm() {
        return "find-email";
    }

    // 이메일 찾기 요청
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

    //이메일 인증번호 발송
    @ResponseBody
    @PostMapping("/api/email/send")
    public ResponseEntity<Void> sendEmail(@RequestBody EmailRequest request) {
        memberClient.sendSignupEmail(request);
        return ResponseEntity.ok().build();
    }

    // 이메일 인증번호 검증
    @ResponseBody
    @PostMapping("/api/email/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody VerifyEmailRequest request) {
        memberClient.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/addresses")
    public String addAddress(@ModelAttribute AddressCreateRequest request) {
        memberClient.addAddress(request);
        return "redirect:/members/my-page";
    }

    @PostMapping("/addresses/{addressId}/delete")
    public String deleteAddress(@PathVariable Long addressId) {
        memberClient.deleteAddress(addressId);
        return "redirect:/members/my-page";
    }

    @PostMapping("/addresses/{addressId}/update")
    public String updateAddress(@PathVariable Long addressId, @ModelAttribute AddressUpdateRequest request) {
        memberClient.updateAddress(addressId, request);
        return "redirect:/members/my-page";
    }

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

    private void forceLogout(HttpServletResponse response) {
        ResponseCookie accessCookie = CookieUtils.deleteCookie("accessToken");
        ResponseCookie refreshCookie = CookieUtils.deleteCookie("refreshToken");

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    // 소셜 회원가입 추가 정보 입력 페이지
    @GetMapping("/social-signup")
    public String socialSignupForm(HttpServletRequest request, Model model) {
        // 쿼리 파라미터로 넘어온 토큰을 쿠키에 저장하는 로직은
        // 별도의 Handler나 Interceptor에서 처리되었다고 가정하거나,
        // 여기서 request.getParameter("accessToken")을 꺼내 쿠키에 심어줘야 할 수도 있습니다.
        // 보통은 /login/oauth2/success 와 비슷한 로직으로 토큰을 먼저 세팅해야 합니다.

        return "social-signup";
    }

    // 소셜 추가 정보 저장 처리
    @PostMapping("/social-signup")
    public String submitSocialInfo(@ModelAttribute SocialSignupRequest request,
                                   HttpServletRequest httpRequest,
                                   HttpServletResponse response) {
        // member Service 호출 (정보 업데이트 & 등급 승격)
        // FeignClient가 헤더에 GUEST용 AccessToken을 싣고 갑니다.
        memberClient.updateSocialMember(request);

        ResponseCookie accessCookie = CookieUtils.deleteCookie("accessToken");
        ResponseCookie refreshCookie = CookieUtils.deleteCookie("refreshToken");

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return "redirect:/auth/login?social_complete=true";
    }



    @GetMapping("/grades")
    public String gradeForm(Model model) {
        List<GradeResponse> grades = memberClient.getGrades();
        model.addAttribute("grades",grades);
        return "grade-list";
    }

    @GetMapping("/orders")
    public String myPageOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        // 정렬은 우선 고정
        String sort = "bookId,desc";
        PageResponse<OrderResponse> orders = orderClient.getAllOrderByMember(page, size, sort);
        model.addAttribute("orders", orders);
        model.addAttribute("activeTab", "orders");
        return "/my/my-orders";
    }

    @GetMapping("/reviews")
    public String getReviews(Model model) {

        return "/my/my-reviews";
    }

}
