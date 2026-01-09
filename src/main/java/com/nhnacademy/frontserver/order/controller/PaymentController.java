package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.cart.client.CartClient; // [추가] CartClient 임포트
import com.nhnacademy.frontserver.order.NonMemberOrderCancelRequest;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.client.OrderClient;
import com.nhnacademy.frontserver.order.client.PaymentClient;
import com.nhnacademy.frontserver.order.util.payment.PaymentCancelRequestDto;
import com.nhnacademy.frontserver.order.util.payment.PaymentRequestDto;
import com.nhnacademy.frontserver.order.util.payment.PaymentResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentClient paymentClient;
    private final OrderClient orderClient;

    @Value("${toss.client.key}")
    private String tossClientKey;

    @GetMapping
    public String paymentSheet(HttpSession session, Model model) {
        OrderResponse order = (OrderResponse) session.getAttribute("orderForPayment");
        if (order == null) {
            return "redirect:/";
        }
        int totalPackagingFee = order.orderItems().stream()
                .mapToInt(item -> item.packagingPrice() * item.quantity())
                .sum();
        model.addAttribute("totalPackagingFee", totalPackagingFee);
        model.addAttribute("order", order);
        model.addAttribute("tossClientKey", tossClientKey);
        return "payment";
    }

    /**
     * 결제 승인 성공 처리
     */
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                 @RequestParam String orderId,
                                 @RequestParam Integer amount,
                                 @RequestParam String provider,
                                 Model model) {
        try {
            // 1. 결제 승인 요청
            PaymentRequestDto requestDto = new PaymentRequestDto(paymentKey, orderId, amount, provider);
            PaymentResponse response = paymentClient.confirmPayment(requestDto);

            if (response != null) {
                model.addAttribute("payment", response);
            }
            return "payment-success";

        } catch (Exception e) {
            log.error(">>>> [결제 승인 로직 실패] orderId={}", orderId, e);
            model.addAttribute("message", "결제 승인 중 오류가 발생했습니다.");
            model.addAttribute("code", "PAYMENT_CONFIRM_ERROR");
            return "payment-fail";
        }
    }

    /**
     * 비회원 결제 취소 및 주문 취소 처리
     */
    @PostMapping("/cancel")
    public String cancelPayment(
            @ModelAttribute PaymentCancelRequestDto cancelDto, // orderNumber, cancelReason, cancelAmount
            @RequestParam("orderId") Long orderId,
            @RequestParam("nonMemberPassword") String nonMemberPassword,
            Model model
    ) {
        log.info(">>>> 결제 취소 요청 진입: {}, orderId={}", cancelDto, orderId);

        try {
            // 1. [결제] 결제 서버에 취소 요청 (환불)
            paymentClient.cancelPayment(cancelDto);
            log.info(">>>> 1. 결제 취소(환불) 완료");

            // 2. [주문] 주문 서버에 취소 요청 (재고 복구 등)
            NonMemberOrderCancelRequest orderCancelRequest = new NonMemberOrderCancelRequest(nonMemberPassword);
            orderClient.cancelOrderByNonMember(orderId, orderCancelRequest);

            log.info(">>>> 2. 주문 취소(재고 복구) 완료");

            // 성공 결과 표시
            model.addAttribute("canceledAmount", cancelDto.cancelAmount());
            model.addAttribute("orderNumber", cancelDto.orderNumber());

            return "payment-cancel-success";

        } catch (Exception e) {
            log.error("결제 및 주문 취소 프로세스 실패", e);
            model.addAttribute("code", "CANCEL_ERROR");
            model.addAttribute("message", "취소 처리 중 오류가 발생했습니다. (결제만 취소되었을 수 있습니다. 고객센터 확인 요망)");
            return "payment-fail";
        }
    }

    @GetMapping("/fail")
    public String paymentFail(@RequestParam String code,
                              @RequestParam String message,
                              Model model) {
        model.addAttribute("code", code);
        model.addAttribute("message", message);
        return "payment-fail";
    }
}