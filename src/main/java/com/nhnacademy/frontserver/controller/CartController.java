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

package com.nhnacademy.frontserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/api/carts")
    public String viewCart(Model model) {

        //TODO 장바구니 통신 구현  * 세부 Response 설계 필요
//        // 1. 장바구니 전체 정보를 담는 객체
//        CartResponse cart = cartService.getCartDetails();
//        model.addAttribute("cart", cart);
//
//        // 2. 장바구니에 담긴 상품 목록
//        List<CartItemResponse> cartItems = cart.getCartItems();
//        model.addAttribute("cartItems", cartItems);
//
//        // 3. (옵션) 회원 정보 (헤더용)
//        MemberResponse member = authService.getCurrentMember();
//        model.addAttribute("member", member);

        return "cart"; // cart.html 렌더링
    }
}
