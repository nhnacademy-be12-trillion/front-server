package com.nhnacademy.frontserver.order;

public record PackagingResponse(
        Long packagingId,
        String packagingType,
        int packagingPrice
) {
}
