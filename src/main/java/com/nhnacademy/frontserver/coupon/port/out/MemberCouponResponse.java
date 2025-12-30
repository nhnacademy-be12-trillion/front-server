package com.nhnacademy.frontserver.coupon.port.out;

import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDateTime;


public record MemberCouponResponse(
        Long id,
        @JsonAlias("coupon_id") Long couponId,
        String name,
        Long quantity,
        @JsonAlias("issue_start_date") LocalDateTime issueStartDate,
        @JsonAlias("issue_end_date") LocalDateTime issueEndDate,
        @JsonAlias("discount_value") Double discountValue,
        @JsonAlias("min_order_price") Long minOrderPrice,
        @JsonAlias("max_discount_price") Long maxDiscountPrice,
        @JsonAlias("coupon_discount_type") String couponDiscountType
) {
}
