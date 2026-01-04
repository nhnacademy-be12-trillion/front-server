package com.nhnacademy.frontserver.member;

import jakarta.validation.constraints.NotNull;

public record MemberAdminUpdateRequest(
        @NotNull Long memberId,
        @NotNull String memberState,
        @NotNull String gradeName
) {}