package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.order.OrderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/my-page/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderClient orderClient;

    @GetMapping
    public String getMyOrders(@PageableDefault(size = 10, sort = "orderId", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        String sortString = pageable.getSort().stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .collect(Collectors.joining(","));

        model.addAttribute("responses", orderClient.getAllOrderByMember(pageable.getPageNumber(), pageable.getPageSize(), sortString));
        model.addAttribute("activeTab", "orders");
        return "my/my-orders";
    }
}
