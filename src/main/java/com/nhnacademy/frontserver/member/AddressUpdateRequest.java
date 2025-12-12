package com.nhnacademy.frontserver.member;

public record AddressUpdateRequest(
        String addressPostCode,
        // 도로명 주소
        String addressBase,
        // 상세 주소
        String addressDetail,
        // 별칭
        String addressAlias
) {
}
