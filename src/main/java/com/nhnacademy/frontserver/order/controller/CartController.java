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

package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.order.CartClient;
import com.nhnacademy.frontserver.order.OrderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartClient cartClient;

    @GetMapping("/api/carts")
    public String viewCart(Model model) {

        //TODO 승현 장바구니 통신 구현  * 세부 Response 설계 필요
        // bookId 를 List로 날려서 bookClient 가 응답해주는 방식? 통신을 계속해서 날려서 Response 를 받는 방식?
        model.addAttribute("cartItems",cartClient.getCarts());


        return "cart"; // cart.html 렌더링
    }
}
