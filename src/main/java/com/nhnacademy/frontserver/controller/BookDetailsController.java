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

import com.nhnacademy.frontserver.dto.book.BookDetailResponse;
import com.nhnacademy.frontserver.dto.book.ReviewResponse;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class BookDetailsController {

    @GetMapping("/api/books/{bookId}")
    public String bookDetail(@PathVariable Long bookId, Model model) {

//        // 1. 도서 상세 정보 (제목, 가격, ISBN, 설명, 목차, 재고, 리뷰 수, 평균 평점 등)
//        BookDetailResponse book = bookService.getBookDetail(bookId);
//        model.addAttribute("book", book);
//
//        // 2. 포장 정책 옵션 (관리자 페이지에서 설정된 값)
//        model.addAttribute("packagingOptions", policyService.getPackagingPolicies());
//
//        // 3. 리뷰 목록
//        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
//        model.addAttribute("reviews", reviews);
//
//        // 4. 회원 정보 (옵션: Header 처리를 위함)
//        model.addAttribute("member", authService.getCurrentMember());

        return "book-detail";
    }

}
