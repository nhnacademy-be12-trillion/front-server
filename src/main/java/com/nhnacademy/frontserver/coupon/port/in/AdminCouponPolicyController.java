package com.nhnacademy.frontserver.coupon.port.in;

import com.nhnacademy.frontserver.common.Page;
import com.nhnacademy.frontserver.coupon.port.out.AdminCouponPolicyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupon-policies")
@Slf4j
public class AdminCouponPolicyController {
    private final AdminCouponPolicyClient adminCouponPolicyClient;
    @GetMapping
    public String getIndexPage(Model model, Page page) {
        model.addAttribute("contents",adminCouponPolicyClient.getPolices(page.pageNumber(), page.pageSize()));
        return "adminCouponPolicy";
    }
    @GetMapping("{coupon-policy-id}")
    public String createCouponPage(@PathVariable(name = "coupon-policy-id")Long id, Model model, Page page) {
        model.addAttribute("content",adminCouponPolicyClient.getPolicy(id));
        return "coupon-create";
    }

}
