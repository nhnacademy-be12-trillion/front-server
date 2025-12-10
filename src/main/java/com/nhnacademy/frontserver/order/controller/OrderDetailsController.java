package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.order.NonMemberOrderGetRequest;
import com.nhnacademy.frontserver.order.OrderClient;
import com.nhnacademy.frontserver.order.OrderItemStatusPatchRequest;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.util.OrderItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        OrderItemStatusPatchRequest request = new OrderItemStatusPatchRequest(OrderItemStatus.CONFIRMED);
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
        OrderItemStatus status;
        if ("CHANGE_OF_MIND".equals(reason)) {
            status = OrderItemStatus.RETURN_REQUESTED_CHANGE_OF_MIND;
        } else if ("DAMAGED".equals(reason)) {
            status = OrderItemStatus.RETURN_REQUESTED_DAMAGED;
        } else {
            // 잘못된 reason -> 일단 리다이렉트
            return "redirect:/orders/" + orderId;
        }
        OrderItemStatusPatchRequest request = new OrderItemStatusPatchRequest(status);
        orderClient.patchOrderItemStatusByMember(orderId, itemId, request);
        return "redirect:/orders/" + orderId;
    }

    @GetMapping("/non-members")
    public String getOrderDetailForNonMember(@RequestParam String orderNumber,
                                             @RequestParam String password,
                                             Model model) {
        NonMemberOrderGetRequest request = new NonMemberOrderGetRequest(orderNumber, password);
        OrderResponse order = orderClient.getOrderByNonMember(request);
        model.addAttribute("order", order);

        return "order-detail";
    }

    @GetMapping("/non-member-orders")
    public String showNonMemberOrderPage() {
        return "non-member-order";
    }
}
