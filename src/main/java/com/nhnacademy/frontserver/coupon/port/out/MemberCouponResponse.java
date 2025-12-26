package com.nhnacademy.frontserver.coupon.port.out;

import java.time.LocalDateTime;


public record MemberCouponResponse(
        Long id,
        Long memberId,
        Long couponId,
        boolean use,
        LocalDateTime lastModifiedDate
) {}
