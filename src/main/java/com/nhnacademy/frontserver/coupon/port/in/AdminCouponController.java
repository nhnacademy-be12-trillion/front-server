package com.nhnacademy.frontserver.coupon.port.in;

import com.nhnacademy.frontserver.common.Page;
import com.nhnacademy.frontserver.coupon.port.out.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/coupons")
@Slf4j
public class AdminCouponController {

    private final AdminCouponClient adminCouponClient;
    private final AdminCouponPolicyClient adminCouponPolicyClient; // 정책 클라이언트 추가

    /**
     * 쿠폰 및 정책 관리 페이지 조회
     */
    @GetMapping
    public String getCouponPage(@RequestParam(required = false, defaultValue = "0") Integer page,
                                @RequestParam(required = false, defaultValue = "10") Integer size,
                                Model model) {
        // 1. 쿠폰 목록 조회
        List<CouponResponse> coupons = adminCouponClient.getCoupons(page, size);

        // 2. 정책 목록 조회 (쿠폰 생성 모달에서 선택지로 사용하기 위함)
        // 페이징이 필요하다면 사이즈를 크게 잡거나 전체 조회 API를 별도로 구성하는 것이 좋으나,
        // 여기서는 예시로 첫 페이지를 넉넉하게 가져옵니다.
        List<CouponPolicyResponse> policies = adminCouponPolicyClient.getPolices(0, 100);

        model.addAttribute("coupons", coupons);
        model.addAttribute("policies", policies);

        return "admin/admin-coupon-list"; // 새로 생성할 HTML 파일명
    }

    /**
     * 쿠폰 정책 생성
     */
    @PostMapping("/policies")
    public String createCouponPolicy(PolicyCreateRequest request) {
        adminCouponPolicyClient.createCouponPolicy(request);
        return "redirect:/admin/coupons";
    }

    /**
     * 쿠폰 생성 (발급)
     */
    @PostMapping
    public String createCoupon(CouponCreateRequest request) {
        adminCouponClient.createCoupon(request);
        return "redirect:/admin/coupons";
    }
}