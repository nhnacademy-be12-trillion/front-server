package com.nhnacademy.frontserver.member.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.CategorySearchResponse;
import com.nhnacademy.frontserver.member.GradeResponse;
import com.nhnacademy.frontserver.member.MemberAdminResponse;
import com.nhnacademy.frontserver.member.MemberAdminUpdateRequest;
import com.nhnacademy.frontserver.member.client.MemberClient;
import com.nhnacademy.frontserver.order.OrderItemStatusPatchRequest;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.client.OrderClient;
import com.nhnacademy.frontserver.order.util.OrderItemStatus;
import com.nhnacademy.frontserver.point.PointPolicyResponse;
import com.nhnacademy.frontserver.point.PointPolicyUpdateRequest;
import com.nhnacademy.frontserver.point.client.PointClient;
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
    private final PointClient pointClient;
    private final MemberClient memberClient;

    // 관리자 메인 리다이렉트
    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/orders";
    }

    // 주문 관리 페이지
    @GetMapping("/admin/orders")
    public String adminOrders(@RequestParam(defaultValue = "0") int page, Model model) {
        List<OrderResponse> orderList = Collections.emptyList();
        int totalPages = 0;
        long totalElements = 0;

        try {
            // 주문 목록은 PageResponse로 반환되므로 기존 로직 유지
            PageResponse<OrderResponse> response = orderClient.getAllOrderByAdmin(page, 20, "orderDetails.orderDate,desc");
            if (response != null && response.content() != null) {
                orderList = response.content();
                totalPages = response.totalPages();
                totalElements = response.totalElements();
            }
        } catch (Exception e) {
            log.error("주문 내역 조회 실패", e);
        }

        model.addAttribute("orders", orderList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("itemStatuses", OrderItemStatus.values());

        Map<String, String> statusMap = new HashMap<>();
        for (OrderItemStatus status : OrderItemStatus.values()) statusMap.put(status.name(), status.getTitle());
        model.addAttribute("statusMap", statusMap);

        return "admin/orders";
    }

    // 정책 관리 페이지 (배송 + 포장 + 포인트 통합)
    @GetMapping("/admin/policies")
    public String adminPolicies(Model model) {
        model.addAttribute("activeMenu", "policies");

        try {
            // 배송비 정책
            model.addAttribute("deliveryPolicy", orderClient.getDeliveryPolicy());

            // 포장 정책
            model.addAttribute("packagingList", orderClient.getAllPackaging(0, 100, "packagingId,asc"));

            // 포인트 정책
            List<PointPolicyResponse> pointPolicies = pointClient.getPolicies();
            model.addAttribute("pointPolicies", pointPolicies);

        } catch (Exception e) {
            log.error("정책 조회 실패", e);
            // 에러 발생 시 빈 리스트라도 넣어 뷰 렌더링 오류 방지
            if (!model.containsAttribute("packagingList")) {
                model.addAttribute("packagingList", Collections.emptyList());
            }
            if (!model.containsAttribute("pointPolicies")) {
                model.addAttribute("pointPolicies", Collections.emptyList());
            }
        }
        return "admin/policies";
    }

    // 회원 관리 페이지
    @GetMapping("/admin/members")
    public String adminMembers(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("activeMenu", "members");

        try {
            // 회원 목록 조회 (페이지 당 10명)
            PageResponse<MemberAdminResponse> response = memberClient.getMembersByAdmin(page, 10);

            model.addAttribute("members", response.content());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", response.totalPages());
            model.addAttribute("totalElements", response.totalElements());

            // 등급 목록 조회
            List<GradeResponse> grades = memberClient.getGrades();
            model.addAttribute("grades", grades);

        } catch (Exception e) {
            log.error("회원 목록 조회 실패", e);
            model.addAttribute("members", Collections.emptyList());
        }

        return "admin/members";
    }

    // 회원 정보 수정 처리 (AJAX)
    @PutMapping("/admin/members")
    @ResponseBody
    public ResponseEntity<String> updateMemberInfo(@RequestBody MemberAdminUpdateRequest request) {
        try {
            // request 안에 memberId, state, grade가 다 들어있음
            memberClient.updateMemberByAdmin(request);
            return ResponseEntity.ok("회원 정보가 수정되었습니다.");
        } catch (Exception e) {
            log.error("회원 수정 실패", e);
            return ResponseEntity.status(500).body("수정 실패: " + e.getMessage());
        }
    }

    // 포인트 정책 수정 처리
    @PostMapping("/admin/policies/points/{policyId}")
    public String updatePointPolicy(@PathVariable Long policyId,
                                    @ModelAttribute PointPolicyUpdateRequest request) {
        try {
            pointClient.updatePointPolicy(policyId, request);
        } catch (Exception e) {
            log.error("포인트 정책 수정 실패", e);
            return "redirect:/admin/policies?error=Update failed";
        }
        return "redirect:/admin/policies?success=Updated";
    }

    // 주문 상세 상태 변경 (AJAX)
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
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("실패: " + e.getMessage());
        }
    }

    // 카테고리 검색 (AJAX)
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