package com.nhnacademy.frontserver.member;

import jakarta.validation.constraints.NotBlank;

public record AddressUpdateRequest(
        @NotBlank String addressPostCode,
        @NotBlank String addressBase,
        @NotBlank String addressDetail,
        @NotBlank String addressAlias
) {}