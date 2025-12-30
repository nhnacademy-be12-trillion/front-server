package com.nhnacademy.frontserver.coupon.port.out;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "gateway-coupon",
        url = "${gateway.url}",
        path = "/api/member-coupons",
        contextId = "memberCoupon"
)
public interface MemberCouponClient {
    @GetMapping("")
    List<MemberCouponResponse> getMemberCoupons(@RequestParam boolean isUse,@RequestParam Integer page, @RequestParam Integer size);
}
