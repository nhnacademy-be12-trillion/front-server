package com.nhnacademy.frontserver.coupon.port.out;

record CouponPolicyResponse(Long id, String name, Long minOrderPrice, Long maxDiscountPrice, Double discountValue, String couponDiscountType) {
}
