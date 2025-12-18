package com.nhnacademy.frontserver.order.client;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.order.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-order",
        url = "${gateway.url}",
        contextId = "orderClient")
public interface OrderClient {

    // 주문 전체 조회 (관리자)
    @GetMapping("/api/orders/admin")
    PageResponse<OrderResponse> getAllOrderByAdmin(@RequestParam("page") int page,
                                                   @RequestParam("size") int size,
                                                   @RequestParam("sort") String sort);

    // 주문 전체 조회 (회원)
    @GetMapping("/api/orders")
    PageResponse<OrderResponse> getAllOrderByMember(@RequestParam("page") int page,
                                                    @RequestParam("size") int size,
                                                    @RequestParam("sort") String sort);

    // 취소된 주문 전체 조회
    @GetMapping("/api/orders/canceled")
    PageResponse<OrderResponse> getAllCanceledOrderByMember(@RequestParam("page") int page,
                                                            @RequestParam("size") int size,
                                                            @RequestParam("sort") String sort);

    // 환불, 환불 요청된 주문 전체 조회
    @GetMapping("/api/order-items/refunds")
    PageResponse<OrderItemResponse> getAllRefundedOrderItemsByMember(@RequestParam("page") int page,
                                                                     @RequestParam("size") int size,
                                                                     @RequestParam("sort") String sort);

    // 주문 단건 조회 (회원)
    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrderByMember(@PathVariable Long orderId);

    // 주문 단건 조회 (비회원) TODO - 조회에 POST 메소드 사용??
    @PostMapping("/api/orders/non-members/")
    OrderResponse getOrderByNonMember(NonMemberOrderGetRequest request);

    // 주문 생성 (회원, 비회원)
    @PostMapping("/api/orders")
    OrderResponse createOrder(@RequestBody OrderCreateRequest request);

    // 주문 상품 상태 변경 (관리자, 회원)
    @PatchMapping("/api/orders/{orderId}/items/{orderItemId}")
    OrderResponse patchOrderItemStatusByMember(@PathVariable Long orderId, @PathVariable Long orderItemId,
                                               @RequestBody OrderItemStatusPatchRequest request);

    // 주문 상품 상태 변경 (비회원)
    @PatchMapping("/api/orders/non-members/{orderId}/items/{orderItemId}")
    OrderResponse patchOrderItemStatusByNonMember(@PathVariable Long orderId, @PathVariable Long orderItemId,
                                                  @RequestBody NonMemberOrderItemStatusPatchRequest request);

    // 주문 취소 (회원)
    @DeleteMapping("/api/orders/{orderId}")
    void cancelOrderByMember(@PathVariable Long orderId);

    // 주문 취소 (비회원)
    @PostMapping("/api/orders/non-members/{orderId}/cancel")
    void cancelOrderByNonMember(@PathVariable Long orderId,
                                @RequestBody NonMemberOrderCancelRequest request);

    // 배송비 조회
    @GetMapping("/api/orders/delivery-policy")
    DeliveryPolicyResponse getDeliveryPolicy();

    // 배송비 설정 (초기 설정 및 수정)
    @PutMapping("/api/orders/delivery-policy")
    void updateDeliveryPolicy(@RequestBody DeliveryPolicyUpdateRequest request);

    // 포장 목록 조회
    @GetMapping("/api/orders/packaging")
    PageResponse<PackagingResponse> getAllPackaging(@RequestParam("page") int page,
                                                     @RequestParam("size") int size,
                                                     @RequestParam("sort") String sort);

    // 포장 생성
    @PostMapping("/api/orders/packaging")
    PackagingResponse createPackaging(@RequestBody PackagingCreateRequest request);

    // 포장 수정
    @PutMapping("/api/orders/packaging/{packagingId}")
    void updatePackaging(@PathVariable Long packagingId,
                         @RequestBody PackagingUpdateRequest request);

    // 포장 삭제
    @DeleteMapping("/api/orders/packaging/{packagingId}")
    void removePackaging(@PathVariable Long packagingId);
}