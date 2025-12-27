package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.common.PageArgumentResolver;
import com.nhnacademy.frontserver.order.*;
import com.nhnacademy.frontserver.order.client.OrderClient;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderClient orderClient;

    @PostMapping
    public String createOrder(@ModelAttribute OrderCreateRequest request,
                              HttpSession session) {

        List<OrderItemCreateRequest> orderItems = request.orderItems();

        if (orderItems == null || orderItems.isEmpty()) {
            log.warn("주문할 상품이 form에 담겨있지 않습니다.");
            return "redirect:/cart";
        }

        // 프론트엔드(JS)에서 이미 합쳐진 주소를 사용 (없을 경우에만 null 처리)
        String fullAddress = request.receiverAddress();
        if (fullAddress == null || fullAddress.isBlank()) {
            log.warn("배송지 주소가 누락되었습니다.");
            // 필요 시 에러 처리 또는 기본값
        }

        // 포인트 및 쿠폰 null 안전 처리
        int safePointUsage = request.pointUsage(); // int는 null일 수 없음 (기본값 0)
        Long safeCouponId = request.couponId();    // null 허용

        OrderCreateRequest finalRequest = new OrderCreateRequest(
                request.ordererName(), request.ordererContact(), request.ordererEmail(), request.deliveryDate(),
                request.receiverName(), request.receiverContact(),
                fullAddress,
                request.receiverPostCode(), request.nonMemberPassword(), 
                safePointUsage,
                safeCouponId, orderItems
        );

        OrderResponse response = orderClient.createOrder(finalRequest);

        session.removeAttribute("checkoutItems");
        session.setAttribute("orderForPayment", response);

        return "redirect:/payments";
    }

    // =====================================================================
    // [비회원 주문 조회 및 취소]
    // =====================================================================

    // 1. 비회원 주문 조회 폼 페이지 이동
    @GetMapping("/non-members-form")
    public String nonMemberOrderForm() {
        return "non-members-order-form";
    }

    // 2. [수정됨] 비회원 주문 조회 처리 (POST)
    @PostMapping("/non-members")
    public String getNonMemberOrder(@RequestParam String orderNumber,
                                    @RequestParam String password,
                                    Model model) {
        try {
            // Client 스펙에 맞는 DTO 생성
            NonMemberOrderGetRequest request = new NonMemberOrderGetRequest(orderNumber, password);

            // Feign Client 호출
            OrderResponse order = orderClient.getOrderByNonMember(request);

            // 뷰에 주문 정보 전달
            model.addAttribute("order", order);

            // [★핵심 추가] 입력한 비밀번호를 뷰로 전달 (나중에 취소할 때 사용하기 위해)
            model.addAttribute("nonMemberPassword", password);

            return "non-member-order-detail";

        } catch (Exception e) {
            log.error("비회원 주문 조회 실패", e);
            model.addAttribute("errorMessage", "주문 정보를 찾을 수 없습니다. 정보를 확인해주세요.");
            return "non-members-order-form";
        }
    }
}