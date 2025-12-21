package com.nhnacademy.frontserver.member;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MemberSignupRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String memberEmail,

        @NotBlank(message = "인증번호를 입력해주세요.")
        String verificationCode,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String memberPassword,

        @NotBlank(message = "이름은 필수입니다.")
        String memberName,

        @NotBlank(message = "연락처는 필수입니다.")
        String memberContact,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate memberBirth,

        @NotNull(message = "주소 정보는 필수입니다.")
        @Valid
        AddressCreateRequest address
) {}