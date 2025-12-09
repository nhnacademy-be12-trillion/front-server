package com.nhnacademy.frontserver.controller;

import com.nhnacademy.frontserver.controller.order.OrderClient;
import com.nhnacademy.frontserver.controller.order.OrderItemStatusPatchRequest;
import com.nhnacademy.frontserver.controller.order.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderDetailsController {

    private final OrderClient orderClient;

    @GetMapping("/{orderId}")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        OrderResponse order = orderClient.getOrderByMember(orderId);
        model.addAttribute("order", order);
        return "order-detail";
    }

    @PostMapping("/{orderId}/items/{itemId}/confirm")
    public String confirmOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        OrderItemStatusPatchRequest request = new OrderItemStatusPatchRequest(com.nhnacademy.frontserver.controller.order.util.OrderItemStatus.CONFIRMED);
        orderClient.patchOrderItemStatusByMember(orderId, itemId, request);
        return "redirect:/orders/" + orderId;
    }

    @PostMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable Long orderId) {
        orderClient.cancelOrderByMember(orderId);
        return "redirect:/api/members/my-page";
    }

    @PostMapping("/{orderId}/items/{itemId}/return")
    public String requestReturnItem(@PathVariable Long orderId, @PathVariable Long itemId, @RequestParam String reason) {
        com.nhnacademy.frontserver.controller.order.util.OrderItemStatus status;
        if ("CHANGE_OF_MIND".equals(reason)) {
            status = com.nhnacademy.frontserver.controller.order.util.OrderItemStatus.RETURN_REQUESTED_CHANGE_OF_MIND;
        } else if ("DAMAGED".equals(reason)) {
            status = com.nhnacademy.frontserver.controller.order.util.OrderItemStatus.RETURN_REQUESTED_DAMAGED;
        } else {
            // 잘못된 reason -> 일단 리다이렉트
            return "redirect:/orders/" + orderId;
        }
        OrderItemStatusPatchRequest request = new OrderItemStatusPatchRequest(status);
        orderClient.patchOrderItemStatusByMember(orderId, itemId, request);
        return "redirect:/orders/" + orderId;
    }
}
