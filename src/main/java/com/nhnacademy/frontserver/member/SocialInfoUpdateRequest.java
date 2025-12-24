package com.nhnacademy.frontserver.member;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record SocialInfoUpdateRequest(
        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate memberBirth,

        @Pattern(regexp = "^01(?:0|1|[2-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "연락처 형식이 올바르지 않습니다.")
        String memberContact,

        @NotNull @Valid
        AddressCreateRequest memberAddress
) {}