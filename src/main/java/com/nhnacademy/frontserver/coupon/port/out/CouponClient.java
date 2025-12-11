package com.nhnacademy.frontserver.coupon.port.out;

import com.nhnacademy.frontserver.coupon.port.in.CouponCreateRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "couponClient",url="http://localhost:14017",path = "/admin/coupons")
public interface CouponClient {
    @PostMapping()
    void createCoupon(@RequestBody CouponCreateRequest request);
    @GetMapping()
    List<CouponResponse> getCoupons(@RequestParam Integer page, @RequestParam Integer size);
}
