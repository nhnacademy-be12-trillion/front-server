package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.client.OrderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/my-page/orders")
@RequiredArgsConstructor
public class MyOrderController {

    private final OrderClient orderClient;

    // 주문 내역 조회 (마이페이지)
    @GetMapping
    public String myPageOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model
    ) {
        String sort = "orderId,desc";
        
        try {
            PageResponse<OrderResponse> orders = orderClient.getAllOrderByMember(page, size, sort);
            model.addAttribute("orders", orders);
        } catch (Exception e) {
            log.error("주문 내역 조회 실패", e);
            model.addAttribute("orders", null); 
        }

        model.addAttribute("activeTab", "orders"); 
        return "my/my-orders";
    }
}