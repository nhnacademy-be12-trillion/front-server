package com.nhnacademy.frontserver.coupon.port.in;

import com.nhnacademy.frontserver.common.Page;
import com.nhnacademy.frontserver.coupon.port.out.MemberCouponClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class CouponController {
    private final MemberCouponClient memberCouponClient;
    @GetMapping("/my-coupons")
    public String getIndexPage(@RequestParam(value = "status", required = false, defaultValue = "unused") String status, Model model, Page page) {

        model.addAttribute("contents",memberCouponClient.getMemberCoupons(isUse(status),page.pageNumber(), page.pageSize()));
        return "my/my-coupons";
    }
    private boolean isUse(String status) {
        return "used".equals(status);
    }
}
