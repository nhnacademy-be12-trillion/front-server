package com.nhnacademy.frontserver.client;

import com.nhnacademy.frontserver.dto.order.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service", url = "http://localhost:10407")
public interface OrderClient {

    // 주문 전체 조회 (관리자)
    @GetMapping("/api/orders/admin")
    Page<OrderResponse> getAllOrderByAdmin(@SpringQueryMap Pageable pageable);

    // 주문 전체 조회 (회원)
    @GetMapping("/api/orders")
    Page<OrderResponse> getAllOrderByMember(@SpringQueryMap Pageable pageable);

    // 주문 단건 조회 (회원)
    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrderByMember(@PathVariable Long orderId);

    // 주문 단건 조회 (비회원)
    @PostMapping("/api/orders/non-members")
    OrderResponse getOrderByNonMember(@RequestBody NonMemberOrderGetRequest request);

    // 주문 생성 (회원, 비회원)
    @PostMapping("/api/orders")
    OrderResponse createOrder(@RequestBody OrderCreateRequest request);

    // 주문 상품 상태 변경 (관리자, 회원)
    @PatchMapping("/api/orders/{orderId}/items/{orderItemId}")
    OrderResponse patchOrderItemStatusByMember(@PathVariable Long orderId, @PathVariable Long orderItemId);

    // 주문 상품 상태 변경 (비회원)
    @PatchMapping("/api/orders/non-members/{orderId}/items/{orderItemId}")
    OrderResponse patchOrderItemStatusByNonMember(@PathVariable Long orderId, @PathVariable Long orderItemId,
                                                  @RequestBody NonMemberOrderItemStatusPatchRequest request);

    // 주문 취소 (회원)
    @DeleteMapping("/api/orders/{orderId}")
    void cancelOrderByMember(@PathVariable Long orderId);

    // 주문 취소 (비회원)
    @DeleteMapping("/api/orders/non-members/{orderId}")
    void cancelOrderByNonMember(@PathVariable Long orderId,
                                @RequestBody NonMemberOrderCancelRequest request);
}
