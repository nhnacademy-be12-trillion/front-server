package com.nhnacademy.frontserver.member;

import java.time.LocalDate;

record MemberResponse(
        Long memberId,
        String memberEmail,
        String memberName,
        String memberContact,
        LocalDate memberBirth,
        MemberState memberState,
        Integer memberPoint,
        String gradeName
) {
}
