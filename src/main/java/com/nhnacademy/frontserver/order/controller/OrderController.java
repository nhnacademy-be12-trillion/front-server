package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.common.PageArgumentResolver;
import com.nhnacademy.frontserver.order.client.OrderClient;
import com.nhnacademy.frontserver.order.OrderCreateRequest;
import com.nhnacademy.frontserver.order.OrderItemCreateRequest;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.CheckoutItemView;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderClient orderClient;
    private final PageArgumentResolver pageArgumentResolver;

    @PostMapping
    public String createOrder(@ModelAttribute OrderCreateRequest request,
                              @RequestParam("address1") String address1,
                              @RequestParam("address2") String address2,
                              HttpSession session) {
        @SuppressWarnings("unchecked")
        List<CheckoutItemView> items = (List<CheckoutItemView>) session.getAttribute("checkoutItems");
        if (items == null || items.isEmpty()) {
            return "redirect:/";
        }

        List<OrderItemCreateRequest> orderItems = items.stream()
                .map(item -> new OrderItemCreateRequest(item.bookId(), item.quantity(), null))
                .collect(Collectors.toList());

        String fullAddress = address1 + " " + address2;

        OrderCreateRequest finalRequest = new OrderCreateRequest(
            request.ordererName(), request.ordererContact(), request.deliveryDate(),
            request.receiverName(), request.receiverContact(), 
            fullAddress,
            request.receiverPostCode(), request.nonMemberPassword(), request.pointUsage(),
            request.couponId(), orderItems
        );

        OrderResponse response = orderClient.createOrder(finalRequest);

        session.removeAttribute("checkoutItems");

        // 결제 페이지로 전달할 주문 정보를 세션에 저장
        session.setAttribute("orderForPayment", response);

        // 결제 페이지로 리다이렉트
        return "redirect:/payments";
    }
}
