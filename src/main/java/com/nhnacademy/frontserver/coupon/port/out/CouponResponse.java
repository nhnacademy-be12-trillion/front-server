package com.nhnacademy.frontserver.coupon.port.out;

import java.time.LocalDateTime;

public record CouponResponse(String name,Long policyId,Long quantity,LocalDateTime issueStartDate,LocalDateTime issueEndDate) {
}
