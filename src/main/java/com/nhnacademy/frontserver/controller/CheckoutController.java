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
public class CheckoutController {

    @GetMapping("/api/orders/checkout")
    public String checkout(Model model) {

        //TODO
//        // 1. 회원 정보 (로그인 상태) - Billing/Shipping 기본값 제공
//        MemberResponse member = authService.getCurrentMember();
//        model.addAttribute("member", member);
//
//        // 2. 장바구니 요약 정보 (결제 금액 및 상품 목록 제공)
//        CartResponse cart = cartService.getCartSummary();
//        model.addAttribute("cart", cart);
//
//        // 3. 주문 요청을 받을 빈 객체 (Binding Result용)
//        model.addAttribute("orderRequest", new OrderRequest());

        return "checkout";
    }
}
