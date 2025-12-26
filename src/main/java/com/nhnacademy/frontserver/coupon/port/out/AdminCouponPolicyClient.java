package com.nhnacademy.frontserver.coupon.port.out;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "gateway-coupon",
        url = "${gateway.url}",
        path = "/admin/coupon-policies",
        contextId = "adminCouponPolicy"
)
public interface AdminCouponPolicyClient {
    @GetMapping
    List<CouponPolicyResponse> getPolices(@RequestParam Integer page, @RequestParam Integer size);
    @GetMapping("/{coupon-policy-id}")
    CouponPolicyResponse getPolicy(@PathVariable(name = "coupon-policy-id") Long id);

}
