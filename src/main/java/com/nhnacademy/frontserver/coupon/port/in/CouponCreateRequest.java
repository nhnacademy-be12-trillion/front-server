package com.nhnacademy.frontserver.coupon.port.in;

import java.time.LocalDateTime;

public record CouponCreateRequest(String name, Long policyId, Long quantity, LocalDateTime issueStartDate, LocalDateTime issueEndDate, String bookName, String categoryName){
}
