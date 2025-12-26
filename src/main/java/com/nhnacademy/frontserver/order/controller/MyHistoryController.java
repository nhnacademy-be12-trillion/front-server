package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.order.OrderItemResponse;
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
@RequestMapping("/my-page/history")
@RequiredArgsConstructor
public class MyHistoryController {

    private final OrderClient orderClient;

    // 취소/반품 내역 조회
    @GetMapping
    public String myPageHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        String sortOrders = "orderId,desc";

        // 1. 취소된 주문 목록 조회
        try {
            PageResponse<OrderResponse> orders = orderClient.getAllCanceledOrderByMember(page, size, sortOrders);
            model.addAttribute("orders", orders);
        } catch (Exception e) {
            log.error("취소된 주문 목록 조회 실패", e);
            model.addAttribute("orders", null);
        }

        String sortOrderItems = "orderItemId,desc";

        // 2. 환불/반품 상품 목록 조회
        try {
            PageResponse<OrderItemResponse> refundedItems = orderClient.getAllRefundedOrderItemsByMember(page, size, sortOrderItems);
            model.addAttribute("refundedItems", refundedItems);
        } catch (Exception e) {
            log.error("환불/반품 상품 목록 조회 실패", e);
            model.addAttribute("refundedItems", null);
        }

        // 3. 배송비 정책 조회 (단순 변심 반품 배송비 계산용)
        try {
            model.addAttribute("deliveryPolicy", orderClient.getDeliveryPolicy());
        } catch (Exception e) {
            log.error("배송비 정책 조회 실패", e);
            model.addAttribute("deliveryPolicy", null);
        }

        model.addAttribute("activeTab", "history");
        return "my/my-history";
    }
}
