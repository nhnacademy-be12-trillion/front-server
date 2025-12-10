package com.nhnacademy.frontserver.cart.client;

import com.nhnacademy.frontserver.cart.dto.CartCreateRequestDto;
import com.nhnacademy.frontserver.cart.dto.CartResponseDto;
import com.nhnacademy.frontserver.cart.dto.CartUpdateRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "cart-service", url = "${cart.api.url}", path = "/api/carts")
public interface CartClient {

    /**
     * [장바구니 담기]
     * POST /api/carts
     */
    @PostMapping
    ResponseEntity<Long> addToCart(
            @RequestHeader(value = "X-User-Id", required = false) Long memberId,
            @RequestHeader(value = "Cookie", required = false) String cookie,
            @RequestBody CartCreateRequestDto request
    );

    /**
     * [장바구니 목록 조회]
     * GET /api/carts
     */
    @GetMapping
    ResponseEntity<List<CartResponseDto>> getCartItems(
            @RequestHeader(value = "X-User-Id", required = false) Long memberId,
            @RequestHeader(value = "Cookie", required = false) String cookie
    );

    /**
     * [수량 변경]
     * PUT /api/carts/{bookId}
     */
    @PutMapping("/{bookId}")
    ResponseEntity<Void> updateCartItem(
            @RequestHeader(value = "X-User-Id", required = false) Long memberId,
            @RequestHeader(value = "Cookie", required = false) String cookie,
            @PathVariable("bookId") Long bookId,
            @RequestBody CartUpdateRequestDto request
    );

    /**
     * [상품 삭제]
     * DELETE /api/carts/{bookId}
     */
    @DeleteMapping("/{bookId}")
    ResponseEntity<Void> removeCartItem(
            @RequestHeader(value = "X-User-Id", required = false) Long memberId,
            @RequestHeader(value = "Cookie", required = false) String cookie,
            @PathVariable("bookId") Long bookId
    );

    /**
     * [장바구니 전체 비우기]
     * DELETE /api/carts
     */
    @DeleteMapping
    ResponseEntity<Void> clearCart(
            @RequestHeader(value = "X-User-Id", required = false) Long memberId,
            @RequestHeader(value = "Cookie", required = false) String cookie
    );

    /**
     * [장바구니 개수 조회]
     * GET /api/carts/count
     */
    @GetMapping("/count")
    ResponseEntity<Long> countCartItems(
            @RequestHeader(value = "X-User-Id", required = false) Long memberId,
            @RequestHeader(value = "Cookie", required = false) String cookie
    );

    /**
     * [장바구니 병합]
     * POST /api/carts/merge
     */
    @PostMapping("/merge")
    ResponseEntity<Void> mergeCart(
            @RequestHeader("X-User-Id") Long memberId,
            @RequestHeader("Cookie") String guestCookie
    );
}