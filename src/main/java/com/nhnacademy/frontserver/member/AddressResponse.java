package com.nhnacademy.frontserver.member;

public record AddressResponse (
        Long addressId,
        String addressPostCode,
        String addressBase,
        String addressDetail,
        String addressAlias
) {
}
