package com.nhnacademy.frontserver.coupon.port.out;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gateway-coupon",
        url = "${gateway.url}",
        path = "/api/book-coupons",
        contextId = "bookCoupon")
public interface BookCouponClient {
    //북쿠폰 조회
    @GetMapping("{book-id}")
    List<CouponResponse> getBookCoupons(@PathVariable("book-id") Long bookId, @RequestParam Integer page, @RequestParam Integer size);
}
