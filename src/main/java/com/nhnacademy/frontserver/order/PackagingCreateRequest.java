package com.nhnacademy.frontserver.order;

public record PackagingCreateRequest(
        String packagingType,
        int packagingPrice
) {
}
