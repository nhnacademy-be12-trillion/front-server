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

package com.nhnacademy.frontserver.controller.book;

import com.nhnacademy.frontserver.controller.PageResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/books")
public class BookListController {

    private final BookClient bookClient;

    public BookListController(BookClient bookClient) {
        this.bookClient = bookClient;
    }

    @GetMapping
    public String getBooks(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            Model model
    ) {
        // 정렬은 우선 고정
        String sort = "bookId,desc";

        // Feign 호출
        PageResponse<BookListResponse> bookPage =
                bookClient.getBooks(page, size, sort);


        model.addAttribute("books", bookPage.content());  // List<BookListResponse>
        model.addAttribute("page", bookPage);                // Page 메타 정보

        return "book-list";
    }
}

