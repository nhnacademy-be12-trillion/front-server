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

package com.nhnacademy.frontserver;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

    private final BookClient bookClient;

    @GetMapping
    public String home(Model model) {

        // 전체 도서 목록
        PageResponse<BookListResponse> books = bookClient.getBooks();
        model.addAttribute("books", books);


        // 인기 도서 (조회수 기준) -> "지금 많이 보고 있는 책"
        List<BookListResponse> popularBooks = bookClient.getPopularBooks();
        model.addAttribute("popularBooks", popularBooks);

        // 카테고리별 신간/추천 도서
        Long targetCategoryId = 1L;
        List<BookListResponse> categoryBooks = bookClient.getBooksByCategory(targetCategoryId);
        model.addAttribute("categoryBooks", categoryBooks);

        return "index";
    }
}
