package com.nhnacademy.frontserver.coupon.port.out;

import java.time.LocalDateTime;


public record MemberCouponResponse(Long id, Long couponId,String name, Long quantity, LocalDateTime issueStartDate, LocalDateTime issueEndDate,Double discountValue, Long minOrderPrice, Long maxDiscountPrice,
                                   String couponDiscountType) {
}
