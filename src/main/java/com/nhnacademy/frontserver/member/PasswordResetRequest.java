package com.nhnacademy.frontserver.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank
        @Email
        String memberEmail,
        @NotBlank
        String verificationCode,
        @NotBlank @Size(min = 8)
        String newPassword
) {}