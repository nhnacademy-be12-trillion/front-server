package com.nhnacademy.frontserver.member;

record AddressCreateRequest(
        String addressPostCode,
        String addressBase,
        String addressDetail,
        String addressAlias
) {}