package com.nhnacademy.frontserver.member;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public record MemberUpdateRequest(
        String memberContact,
        String memberName,
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate memberBirth
){}