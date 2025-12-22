package com.nhnacademy.frontserver.member;

import java.time.LocalDate;

public record MemberResponse(
        Long memberId,
        String memberEmail,
        String memberName,
        String memberContact,
        LocalDate memberBirth,
        String memberState, // ENUM -> String
        Integer memberPoint,
        String gradeName    // ENUM -> String
) {}