package com.nhnacademy.frontserver.member;
import java.time.LocalDate;

public record MemberAdminResponse(
        Long memberId,
        String memberEmail,
        String memberName,
        String memberContact,
        String gradeName,
        String memberRole,
        String memberState,
        LocalDate memberLatestLoginAt
) {}