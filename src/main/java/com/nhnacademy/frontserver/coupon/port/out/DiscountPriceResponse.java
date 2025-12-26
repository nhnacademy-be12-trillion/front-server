package com.nhnacademy.frontserver.coupon.port.out;

import java.util.List;


public record DiscountPriceResponse (List<DiscountPriceResponse> discountPriceResponses,Long totalDiscountPrice) {
}