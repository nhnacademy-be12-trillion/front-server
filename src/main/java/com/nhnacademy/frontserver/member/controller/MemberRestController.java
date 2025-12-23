package com.nhnacademy.frontserver.member.controller;

import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.EmailRequest;
import com.nhnacademy.frontserver.member.VerifyEmailRequest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/members/api") // Nginx가 프론트로 보내는 /members 경로 하위에 둠
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberClient memberClient;

    // 회원가입용 이메일 인증번호 발송
    @PostMapping("/email/send")
    public ResponseEntity<String> sendSignupEmail(@RequestBody EmailRequest request) {
        try {
            log.info("인증번호 발송 요청: {}", request.memberEmail());
            memberClient.sendSignupEmail(request);
            return ResponseEntity.ok("인증번호가 발송되었습니다.");

        } catch (FeignException.Conflict e) {
            // 409 Conflict 에러 발생 시 브라우저에서 이메일 중복 에러 처리
            log.warn("이메일 중복 발생: {}", request.memberEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 가입된 이메일입니다.");

        } catch (Exception e) {
            log.error("인증번호 발송 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 이메일 인증번호 검증
    @PostMapping("/email/verify")
    public ResponseEntity<Void> verifyEmail(@RequestBody VerifyEmailRequest request) {
        log.info("인증번호 검증 요청: {}", request.memberEmail());
        memberClient.verifyEmail(request);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 재설정용 이메일 발송
    @PostMapping("/password/email")
    public ResponseEntity<String> sendResetPasswordEmail(@RequestBody Map<String, String> body) {
        String email = body.get("memberEmail");
        try {
            memberClient.sendResetPasswordEmail(new EmailRequest(email));
            return ResponseEntity.ok("인증번호가 발송되었습니다.");
        } catch (Exception e) {
            log.error("비밀번호 재설정 이메일 발송 실패", e);
            return ResponseEntity.badRequest().body("가입되지 않은 이메일이거나 오류가 발생했습니다.");
        }
    }
}