/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2025. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.frontserver.cart.controller;

import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.*;
import com.nhnacademy.frontserver.common.AuthConst;
import com.nhnacademy.frontserver.layout.cartbadge.service.CartBadgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
// TODO, 타 API 클라이언트 추가해서 필요한 정보 좀더 담아야함...
// TODO, 프론트 서버가 늘어나면? => 로드밸런싱으로 인한 문제점 고려필요
public class CartWebController {

    private final CartClient cartClient;


    /**
     * [장바구니 페이지 조회]
     */
    @GetMapping
    public String viewCartList(Model model,
                               @RequestHeader(value = AuthConst.HEADER_MEMBER_ID, required = false) Long memberId,
                               @CookieValue(value = AuthConst.COOKIE_GUEST_ID, required = false) String guestId) {

        // 1. 목록 조회
        List<CartResponseDto> cartItems = cartClient.getCartItems().getBody();
        model.addAttribute("cartItems", cartItems);

        // 2. 요약 정보 조회 (종류, 총 개수)
        CartSummaryResponseDto cartSummary = cartClient.getCartSummary().getBody();
        model.addAttribute("cartSummary", cartSummary);

        return "cart/list";
    }

    /**
     * [동작] 장바구니 담기
     */
    @PostMapping("/add")
    public String addToCart(@RequestBody CartCreateRequestDto requestDto,
                            @RequestHeader(value = AuthConst.HEADER_MEMBER_ID, required = false) Long memberId,
                            @CookieValue(value = AuthConst.COOKIE_GUEST_ID, required = false) String guestId) {

        // (백엔드 호출)
        cartClient.addToCart(requestDto);

        return "redirect:/carts";
    }

    /**
     * [동작] 수량 변경
     */
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long bookId, @RequestParam Integer quantity,
                                 @RequestHeader(value = AuthConst.HEADER_MEMBER_ID, required = false) Long memberId,
                                 @CookieValue(value = AuthConst.COOKIE_GUEST_ID, required = false) String guestId) {

        cartClient.updateCartItem(bookId, new CartUpdateRequestDto(quantity));

        return "redirect:/carts";
    }

    /**
     * [동작] 삭제
     */
    @PostMapping("/delete/{bookId}")
    public String deleteItem(@PathVariable Long bookId,
                             @RequestHeader(value = AuthConst.HEADER_MEMBER_ID, required = false) Long memberId,
                             @CookieValue(value = AuthConst.COOKIE_GUEST_ID, required = false) String guestId) {

        cartClient.removeCartItem(bookId);

        return "redirect:/carts";
    }

    /**
     * [동작] 비우기
     */
    @PostMapping("/clear")
    public String clearCart(@RequestHeader(value = AuthConst.HEADER_MEMBER_ID, required = false) Long memberId,
                            @CookieValue(value = AuthConst.COOKIE_GUEST_ID, required = false) String guestId) {

        cartClient.clearCart();

        return "redirect:/carts";
    }
}