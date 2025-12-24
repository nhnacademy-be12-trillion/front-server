package com.nhnacademy.frontserver.member;

import jakarta.validation.constraints.NotBlank;

public record FindMemberIdRequest(
        @NotBlank
        String memberName,
        @NotBlank
        String memberContact
) {}
