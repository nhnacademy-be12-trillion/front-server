package com.nhnacademy.frontserver.member;

import jakarta.validation.constraints.NotBlank;

public record AddressCreateRequest(
        @NotBlank(message = "우편번호는 필수입니다.")
        String addressPostCode,
        @NotBlank(message = "기본 주소는 필수입니다.")
        String addressBase,
        @NotBlank(message = "상세 주소는 필수입니다.")
        String addressDetail,
        @NotBlank
        String addressAlias
) {}