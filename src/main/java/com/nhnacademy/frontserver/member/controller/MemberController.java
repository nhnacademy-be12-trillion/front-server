package com.nhnacademy.frontserver.member.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.order.OrderClient;
import com.nhnacademy.frontserver.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberClient memberClient;
    private final OrderClient orderClient;

    @GetMapping
    public String getMemberProfile(Model model) {
        model.addAttribute("member", memberClient.getMember());
        return "/my/my-profile";
    }

    @GetMapping("/addresses")
    public String getAddresses(Model model) {
        model.addAttribute("addresses", memberClient.getAddresses());
        return "/my/my-addresses";
    }

    @GetMapping("/reviews")
    public String getReviews(Model model) {

        return "/my/my-reviews";
    }

    @GetMapping("/orders")
    public String myPageOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        // 정렬은 우선 고정
        String sort = "bookId,desc";
        PageResponse<OrderResponse> orders = orderClient.getAllOrderByMember(page, size, sort);
        model.addAttribute("orders", orders);
        model.addAttribute("activeTab", "orders");
        return "/my/my-orders";
    }

}
