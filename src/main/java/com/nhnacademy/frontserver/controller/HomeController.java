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

import com.nhnacademy.frontserver.controller.book.BookClient;
import com.nhnacademy.frontserver.controller.book.BookListResponse;
import com.nhnacademy.frontserver.dto.book.CategoryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeController {

    BookClient bookClient;

    @GetMapping("/")
    public String home(Model model) {

        // 1. 도서 리스트 (가져올 데이터가 없으면 빈 리스트 혹은 null)
        List<BookListResponse> books = bookClient.getBooks();
        model.addAttribute("books", books); // null이면 화면에 "추후 추가될 예정" 출력

        // 2. 카테고리 리스트
        List<CategoryResponse> categories = bookClient.getCategories();
        model.addAttribute("categories", categories);

        return "index";
    }
}
