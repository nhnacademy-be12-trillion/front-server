package com.nhnacademy.frontserver.coupon.port.out;

import java.time.LocalDateTime;


public record MemberCouponResponse(Long id, String name, Long quantity, LocalDateTime issueStartDate, LocalDateTime issueEndDate, Long minOrderPrice, Long maxDiscountPrice,
                                   CouponDiscountType couponDiscountType) {
}
