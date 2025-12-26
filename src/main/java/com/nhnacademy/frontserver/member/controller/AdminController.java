package com.nhnacademy.frontserver.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.CategorySearchResponse;
import com.nhnacademy.frontserver.order.OrderItemStatusPatchRequest;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.client.OrderClient;
import com.nhnacademy.frontserver.order.util.OrderItemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final BookClient bookClient;
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;

    // =================================================================================
    // 1. 관리자 페이지 화면 (주문, 정책, 회원)
    // =================================================================================

    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
        // ... (기존 주문 조회 로직 유지) ...
        List<OrderResponse> orderList = Collections.emptyList();
        try {
            PageResponse<OrderResponse> response = orderClient.getAllOrderByAdmin(0, 200, "orderDetails.orderDate,desc");
            if (response != null && response.content() != null) {
                orderList = response.content();
            }
        } catch (Exception e) {
            log.error("주문 내역 조회 실패", e);
        }

        model.addAttribute("orders", orderList);
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("itemStatuses", OrderItemStatus.values());

        Map<String, String> statusMap = new HashMap<>();
        for (OrderItemStatus status : OrderItemStatus.values()) statusMap.put(status.name(), status.getTitle());
        // OrderStatus 등 나머지 매핑 로직 유지

        model.addAttribute("statusMap", statusMap);
        return "admin/orders";
    }

    @GetMapping("/admin/policies")
    public String adminPolicies(Model model) {
        model.addAttribute("activeMenu", "policies");
        try {
            model.addAttribute("deliveryPolicy", orderClient.getDeliveryPolicy());
            model.addAttribute("packagingList", orderClient.getAllPackaging(0, 100, "packagingId,asc"));
        } catch (Exception e) {
            log.error("정책 조회 실패", e);
        }
        return "admin/policies";
    }

    @GetMapping("/admin/members")
    public String adminMembers(Model model) {
        model.addAttribute("activeMenu", "members");
        return "admin/members";
    }

    // [제거됨] adminBookRegister (도서 등록 페이지) -> AdminBookController로 이동
    // [제거됨] createBook (도서 등록 처리) -> AdminBookController로 이동
    // [제거됨] getBookInfoByIsbn (ISBN 조회) -> AdminBookController로 이동

    // =================================================================================
    // 2. 기타 공통 API
    // =================================================================================

    // 주문 상태 변경 API
    @PatchMapping("/admin/orders/{orderId}/items/{orderItemId}/status")
    @ResponseBody
    public ResponseEntity<String> updateOrderItemStatus(
            @PathVariable("orderId") Long orderId,
            @PathVariable("orderItemId") Long orderItemId,
            @RequestParam("status") OrderItemStatus status
    ) {
        try {
            OrderItemStatusPatchRequest request = new OrderItemStatusPatchRequest(status);
            orderClient.patchOrderItemStatusByMember(orderId, orderItemId, request);
            return ResponseEntity.ok("상태 변경 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("상태 변경 실패: " + e.getMessage());
        }
    }

    // 카테고리 검색 API (HTML에서 사용하는 URL 유지를 위해 여기에 남겨둠)
    @GetMapping("/admin/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchResponse>> searchCategories(@RequestParam("keyword") String keyword) {
        try {
            return ResponseEntity.ok(bookClient.searchCategories(keyword));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}