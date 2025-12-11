package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.order.*;
import com.nhnacademy.frontserver.order.util.OrderItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderDetailsController {

    private final OrderClient orderClient;

    @GetMapping("/{orderId}")
    public String getOrderDetail(@PathVariable Long orderId, Model model) {
        OrderResponse order = orderClient.getOrderByMember(orderId);
        model.addAttribute("order", order);
        model.addAttribute("isMember", true);
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
                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("orderNumber", orderNumber);
        redirectAttributes.addFlashAttribute("password", password);
        return "redirect:/orders/non-members/details";
    }

    @GetMapping("/non-members/details")
    public String showNonMemberOrderDetailPage(@RequestParam String orderNumber,
                                               @ModelAttribute("password") String password,
                                               Model model) {
        if (password == null || password.isEmpty()) {
            return "redirect:/orders/non-members-form";
        }

        NonMemberOrderGetRequest request = new NonMemberOrderGetRequest(orderNumber, password);
        OrderResponse order = orderClient.getOrderByNonMember(request);

        model.addAttribute("order", order);
        model.addAttribute("isMember", false);
        model.addAttribute("password", password);
        return "order-detail";
    }

    @GetMapping("/non-members-form")
    public String showNonMemberOrderPage() {
        return "non-member-order";
    }

    @PostMapping("/non-members/{orderId}/cancel")
    public String cancelOrderForNonMember(@PathVariable Long orderId, @RequestParam("password") String password) {
        NonMemberOrderCancelRequest request = new NonMemberOrderCancelRequest(password);
        orderClient.cancelOrderByNonMember(orderId, request);
        return "redirect:/orders/non-members-form";
    }

    @PostMapping("/non-members/{orderId}/items/{itemId}/return")
    public String requestReturnItemForNonMember(@PathVariable Long orderId,
                                                @PathVariable Long itemId,
                                                @RequestParam String reason,
                                                @RequestParam String password,
                                                @RequestParam String orderNumber,
                                                RedirectAttributes redirectAttributes) {
        OrderItemStatus status;
        if ("CHANGE_OF_MIND".equals(reason)) {
            status = OrderItemStatus.RETURN_REQUESTED_CHANGE_OF_MIND;
        } else if ("DAMAGED".equals(reason)) {
            status = OrderItemStatus.RETURN_REQUESTED_DAMAGED;
        } else {
            redirectAttributes.addAttribute("orderNumber", orderNumber);
            redirectAttributes.addFlashAttribute("password", password);
            return "redirect:/orders/non-members/details";
        }

        NonMemberOrderItemStatusPatchRequest request = new NonMemberOrderItemStatusPatchRequest(password, status);
        orderClient.patchOrderItemStatusByNonMember(orderId, itemId, request);

        redirectAttributes.addAttribute("orderNumber", orderNumber);
        redirectAttributes.addFlashAttribute("password", password);
        return "redirect:/orders/non-members/details";
    }
}
