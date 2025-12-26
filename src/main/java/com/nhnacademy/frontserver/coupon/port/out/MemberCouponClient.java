package com.nhnacademy.frontserver.coupon.port.out;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "gateway-coupon",
        url = "${gateway.url}",
        path = "/member-coupons",
        contextId = "memberCoupon"
)
public interface MemberCouponClient {
    @GetMapping("")
    List<MemberCouponResponse> getMemberCoupons(Pageable pageable);
}
