package com.nhnacademy.frontserver.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DormantVerifyRequest(
        @NotBlank
        @Email
        String memberEmail,
        @NotBlank
        String verificationCode
) {
}
