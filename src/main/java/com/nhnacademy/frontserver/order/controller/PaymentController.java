package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.order.OrderResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @GetMapping
    public String paymentSheet(HttpSession session, Model model) {
        
        OrderResponse order = (OrderResponse) session.getAttribute("orderForPayment");
        if (order == null) {
            // 세션에 결제할 주문 정보가 없으면 메인으로 리다이렉트
            return "redirect:/";
        }
        
        model.addAttribute("order", order);
        
        // 결제 페이지에서 정보를 사용한 후 세션에서 제거
        session.removeAttribute("orderForPayment");

        return "payment"; // templates/payment.html
    }
}
