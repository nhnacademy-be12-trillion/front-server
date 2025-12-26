package com.nhnacademy.frontserver.coupon.port.out;

record PolicyCreateRequest(String name,Double discountValue, Long minOrderPrice, Long maxDiscountPrice, CouponDiscountType couponDiscountType) {
}
