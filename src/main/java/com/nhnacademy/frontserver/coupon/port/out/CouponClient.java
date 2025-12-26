package com.nhnacademy.frontserver.coupon.port.out;

import com.nhnacademy.frontserver.coupon.port.in.CouponCreateRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "gateway-coupon",
        url = "${gateway.url}",
        path = "/coupons",
        contextId = "coupon"
)
public interface CouponClient {
    @PostMapping()
    void createCoupon(@RequestBody CouponCreateRequest request);
    //할인가격 조회
    @GetMapping("/{coupon-id}")
    public DiscountPriceResponse getDiscountPrice(@PathVariable("coupon-id") Long couponId,
                                                  @RequestParam List<Long> bookIds,
                                                  @RequestParam List<Long> quantities);
}