package com.nhnacademy.frontserver.order;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.infra.FeignOkHttpConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ORDER-SERVICE", url = "http://localhost:10407",
        contextId = "orderClient", configuration = FeignOkHttpConfig.class)
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

    // 주문 단건 조회 (회원)
    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrderByMember(@PathVariable Long orderId);

    // 주문 단건 조회 (비회원) TODO - 조회에 POST 메소드 사용??
    @PostMapping("/api/orders/non-members/")
    OrderResponse getOrderByNonMember(NonMemberOrderGetRequest request);

    // 주문 생성 (회원, 비회원)
    @PostMapping
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



}
