package com.nhnacademy.frontserver.coupon.port.in;

import com.nhnacademy.frontserver.common.Page;
import com.nhnacademy.frontserver.coupon.port.out.MemberCouponClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor
public class CouponController {
    private final MemberCouponClient memberCouponClient;
    @GetMapping("/coupons")
    public String getIndexPage(Model model, Page page) {
        model.addAttribute("contents",memberCouponClient.getMemberCoupons(page.pageNumber(), page.pageSize()));
        return "my/my-coupons";
    }
}
