package com.nhnacademy.frontserver.member;

import java.time.LocalDate;

public record MemberSignupRequest(
        String memberEmail,
        String verificationCode,
        String memberPassword,
        String memberName,
        String memberContact,
        LocalDate memberBirth, // YYYY-MM-DD
        AddressCreateRequest address
) {
}
