package com.nhnacademy.frontserver.layout.cartbadge.controller;

import com.nhnacademy.frontserver.common.AuthConst;
import com.nhnacademy.frontserver.layout.cartbadge.service.CartBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ajax/cart") // [변경] /api/cart -> /ajax/cart
@RequiredArgsConstructor
public class CartBadgeController {

    private final CartBadgeService cartBadgeService;

    // 최종 URL: GET /ajax/cart/badge-count
    @GetMapping("/badge-count") // [변경] count -> badge-count (좀 더 명확하게)
    public ResponseEntity<Integer> getCartCount(
            @RequestHeader(value = AuthConst.HEADER_MEMBER_ID, required = false) Long memberId,
            @CookieValue(value = AuthConst.COOKIE_GUEST_ID, required = false) String guestId) {

        return ResponseEntity.ok(cartBadgeService.getCartCount(memberId, guestId));
    }
}