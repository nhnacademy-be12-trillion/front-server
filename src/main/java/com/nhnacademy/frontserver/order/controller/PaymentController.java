package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.client.PaymentClient;
import com.nhnacademy.frontserver.order.util.payment.PaymentRequestDto;
import com.nhnacademy.frontserver.order.util.payment.PaymentResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentClient paymentClient;

    @Value("${toss.client.key}")
    private String tossClientKey;

    @GetMapping
    public String paymentSheet(HttpSession session, Model model) {
        OrderResponse order = (OrderResponse) session.getAttribute("orderForPayment");
        if (order == null) {
            return "redirect:/";
        }
        model.addAttribute("order", order);
        model.addAttribute("tossClientKey", tossClientKey);
        return "payment";
    }

    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam Integer amount,
                                 @RequestParam String provider,
                                 Model model) {

        log.info(">>>> [결제 성공 리다이렉트] orderId={}, amount={}", orderId, amount);

        try {
            // DTO 생성 (백엔드 스펙에 맞춤)
            PaymentRequestDto requestDto = new PaymentRequestDto(paymentKey, orderId, amount, provider);

            // 백엔드 요청 로그
            log.info(">>>> [Backend 요청] {}", requestDto);

            PaymentResponse response = paymentClient.confirmPayment(requestDto);

            // 응답 로그 확인 (여기서 null이 찍히는지 확인 필요)
            log.info(">>>> [Backend 응답] {}", response);

            if (response != null) {
                model.addAttribute("payment", response);
            } else {
                log.warn(">>>> [주의] 결제는 성공했으나 Backend 응답이 NULL입니다.");
            }

            return "payment-success";

        } catch (Exception e) {
            log.error(">>>> [결제 승인 로직 실패] orderId={}", orderId, e);
            model.addAttribute("message", "결제 승인 중 오류가 발생했습니다.");
            model.addAttribute("code", "PAYMENT_CONFIRM_ERROR");
            return "payment-fail";
        }
    }

    @GetMapping("/fail")
    public String paymentFail(@RequestParam String code,
                              @RequestParam String message,
                              Model model) {
        log.error(">>>> [토스 리다이렉트 실패] code={}, message={}", code, message);
        model.addAttribute("code", code);
        model.addAttribute("message", message);
        return "payment-fail";
    }
}