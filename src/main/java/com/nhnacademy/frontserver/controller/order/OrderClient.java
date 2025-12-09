package com.nhnacademy.frontserver.controller.order;

import com.nhnacademy.frontserver.controller.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service", url = "http://localhost:10407")
public interface OrderClient {

    // 주문 전체 조회 (관리자)
    @GetMapping("/api/orders/admin")
    PageResponse<OrderResponse> getAllOrderByAdmin(@RequestParam("page") int page,
                                                   @RequestParam("size") int size,
                                                   @RequestParam("sort") String sort);

    // 주문 전체 조회 (회원)
    @GetMapping
    PageResponse<OrderResponse> getAllOrderByMember(@RequestParam("page") int page,
                                                    @RequestParam("size") int size,
                                                    @RequestParam("sort") String sort);

    // 주문 단건 조회 (회원)
    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrderByMember(@PathVariable Long orderId);

    // 주문 단건 조회 (비회원) TODO - 조회에 POST 메소드 사용??
    @PostMapping("/api/orders/non-members")
    OrderResponse getOrderByNonMember(@RequestBody NonMemberOrderGetRequest request);

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
//    @DeleteMapping("/api/orders/non-members/{orderId}")
//    void cancelOrderByNonMember(@PathVariable Long orderId,
//                                @RequestBody NonMemberOrderCancelRequest request);

    //TODO 지훈 PaymentRequestDto 확인, API 경로 논의필요, 결제취소 amount 파라미터 논의
//    @PostMapping("/payments")
//    void createPayment(@RequestBody PaymentRequestDto paymentRequestDto);

    @GetMapping("/api/orders/{orderId}/payment")
    PaymentResponse getPayment(@PathVariable Long orderId);

    @DeleteMapping("/api/orders/{orderId}/payment")
    void deletePayment(@PathVariable Long orderId);


}
