package com.nhnacademy.frontserver.member.client;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.member.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "gateway-member",
        url = "${gateway.url}",
        contextId = "memberClient")
public interface MemberClient {
    // 회원
    @PostMapping("/api/members/signup")
    void signup(@RequestBody MemberSignupRequest request);

    @GetMapping("/api/members")
    MemberResponse getMember();

    @GetMapping("/api/members/social/{oauthId}")
    MemberResponse getMemberByOauthId(@PathVariable("oauthId") String oauthId);

    // 마이페이지 정보 수정
    @PutMapping("/api/members")
    void updateMember(@RequestBody MemberUpdateRequest request);

    // 휴면 해제 인증번호 요청
    @PostMapping("/api/members/dormant/request")
    void requestDormantCode(@RequestBody DormantCodeRequest request);

    // 휴면 해제 검증 및 상태 변경
    @PostMapping("/api/members/dormant/verify")
    void verifyDormantCode(@RequestBody DormantVerifyRequest request);

    // 소셜 로그인애서 추가 정보 입력하면 GUEST -> MEMBER로 변경
    @PostMapping("/api/members/social-info")
    void signupSocialMember(@RequestBody SocialSignupRequest request);

    @PutMapping("/api/members/social-info")
    void updateSocialMember(@RequestBody SocialSignupRequest request);

    @PutMapping("/api/members/withdraw")
    void withdrawMember();

    // 비밀번호 재설정용 인증번호 발송 (가입된 이메일인지 체크)
    @PostMapping("/api/members/emails/password")
    void sendResetPasswordEmail(@RequestBody EmailRequest request);

    // 비밀번호 재설정 (인증코드 검증 + 비밀번호 변경을 한번에 수행)
    @PutMapping("/api/members/password/reset")
    void resetPassword(@RequestBody PasswordResetRequest request);

    @PostMapping("/api/members/findEmail")
    String findEmail(@RequestBody FindMemberIdRequest request);

    // 이메일 인증
    @PostMapping("/api/members/emails/signup")
    void sendSignupEmail(@RequestBody EmailRequest request);

    @PostMapping("/api/members/emails/verify")
    void verifyEmail(@RequestBody VerifyEmailRequest request);

    // 주소
    @GetMapping("/api/members/addresses")
    List<AddressResponse> getAllAddresses();

    @PostMapping("/api/members/addresses")
    void addAddress(@RequestBody AddressCreateRequest request);

    @DeleteMapping("/api/members/addresses/{addressId}")
    void deleteAddress(@PathVariable Long addressId);

    @PutMapping("/api/members/addresses/{addressId}")
    void updateAddress(@PathVariable Long addressId, @RequestBody AddressUpdateRequest request);

    // 등급
    @GetMapping("/api/members/grades")
    List<GradeResponse> getGrades();

    // 전체 회원 조회 (관리자)
    @GetMapping("/api/members/admin")
    PageResponse<MemberAdminResponse> getMembersByAdmin(@RequestParam("page") int page,
                                                   @RequestParam("size") int size);
    // 회원 등급/상태 수정 (관리자)
    @PutMapping("/api/members/admin")
    void updateMemberByAdmin(@RequestBody MemberAdminUpdateRequest request);
}
