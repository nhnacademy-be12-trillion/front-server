package com.nhnacademy.frontserver.member;

import java.time.LocalDate;

public record MemberUpdateRequest(
        String memberContact,
        String memberName,
        LocalDate memberBirth
){}