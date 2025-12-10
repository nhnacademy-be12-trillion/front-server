package com.nhnacademy.frontserver.member;

import java.time.LocalDate;

record MemberUpdateRequest(
        String memberContact,
        String memberName,
        LocalDate memberBirth
){}
