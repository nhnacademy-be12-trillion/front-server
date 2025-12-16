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

package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import java.util.List;
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
    public String getBooks(Model model,
                           @RequestParam(name="page", defaultValue="0") int page,
                           @RequestParam(name="size", defaultValue="20") int size,
                           @RequestParam(name="sort", defaultValue="bookId,desc") String sort) {

        PageResponse<BookListResponse> bookPage = bookClient.getBooks(page, size, sort);

        model.addAttribute("books", bookPage.content()); // book-list.html이 List로 쓰는 구조면 OK
        model.addAttribute("page", bookPage);

        return "book-list";
    }


    @GetMapping("/popularBooks")
    public List<BookListResponse> firstPageList(Model model) {
        List<BookListResponse> popularBooks = bookClient.getPopularBooks();
        model.addAttribute("books", popularBooks);
        return popularBooks;
    }
}

