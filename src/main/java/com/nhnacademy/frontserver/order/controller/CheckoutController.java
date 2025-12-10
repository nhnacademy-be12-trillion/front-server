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

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.order.CartClient;
import com.nhnacademy.frontserver.order.CartResponse;
import com.nhnacademy.frontserver.order.CheckoutItemView;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartClient cartClient;
    private final BookClient bookClient;

    @GetMapping("/api/orders/checkout")
    public String checkout(
            @RequestParam(value = "bookId", required = false) Long bookId,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            Model model
    ) {
        List<CheckoutItemView> items = new ArrayList<>();

        // 1) 즉시 결제 흐름: bookId + quantity 가 넘어온 경우
        if (bookId != null && quantity != null) {
            BookDetailResponse book = bookClient.getBookDetail(bookId);

            CheckoutItemView item = new CheckoutItemView(
                    bookId,
                    book.bookName(),
                    book.bookImage(),
                    book.bookSalePrice(),
                    quantity,
                    book.bookSalePrice() * quantity
            );
            items.add(item);
        }
        // 2) 장바구니 결제 흐름: 파라미터 없는 경우 → CartClient 사용
        else {
            List<CartResponse> carts = cartClient.getCarts();

            for (CartResponse cart : carts) {
                BookDetailResponse book = bookClient.getBookDetail(cart.bookId());

                int qty = cart.cartQuantity();
                int unitPrice = book.bookSalePrice();
                CheckoutItemView item = new CheckoutItemView(
                        cart.bookId(),
                        book.bookName(),
                        book.bookImage(),
                        unitPrice,
                        qty,
                        unitPrice * qty
                );
                items.add(item);
            }
        }

        // 총 금액 계산
        int totalAmount = items.stream()
                .mapToInt(CheckoutItemView::totalPrice)
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("totalAmount", totalAmount);

        return "checkout";   // templates/checkout.html
    }
}
