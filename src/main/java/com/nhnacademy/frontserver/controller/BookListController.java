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

import com.nhnacademy.frontserver.dto.book.CategoryResponse;
import java.awt.print.Pageable;
import java.util.List;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookListController {

    @GetMapping("/api/books")
    public String bookList(Model model,
                           @PageableDefault(size = 9) Pageable pageable, // 페이지네이션 처리
                           @RequestParam(required = false) Long categoryId) {

//        // 1. 도서 목록 (Page 객체로 반환)
//        Page<BookResponse> bookPage = bookService.findAll(pageable, categoryId);
//        model.addAttribute("books", bookPage);
//
//        // 2. 카테고리 목록 (사이드바 필터용)
//        List<CategoryResponse> categories = categoryService.getAllCategories();
//        model.addAttribute("categories", categories);
//
//        // 3. 멤버 정보
//        MemberResponse member = authService.getCurrentMember();
//        model.addAttribute("member", member);

        return "books"; // books.html 렌더링
    }
}
