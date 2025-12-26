package com.nhnacademy.frontserver.coupon.port.out;

import java.util.List;

public record CouponUseRequest(List<Long> bookIds, List<Long> quantities) {}
