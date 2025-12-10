package com.nhnacademy.frontserver.coupon.port.in;

import com.nhnacademy.frontserver.common.Page;
import com.nhnacademy.frontserver.coupon.port.out.CouponClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
@Slf4j
public class AdminCouponController {
    private final CouponClient couponClient;

    @PostMapping
    public String createCoupon(CouponCreateRequest request) {
        couponClient.createCoupon(request);
        return "redirect:/admin/coupon-policies";
    }
    @GetMapping
    public String getIndexPage(Page page, Model model) {
        model.addAttribute("contents",couponClient.getCoupons(page.pageNumber(),page.pageSize()));
        return "admin-coupon-list";
    }
}
