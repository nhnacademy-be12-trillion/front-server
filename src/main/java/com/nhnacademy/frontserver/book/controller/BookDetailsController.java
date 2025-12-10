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
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.book.ReviewResponse;
import com.nhnacademy.frontserver.book.ReviewSummaryClient;
import com.nhnacademy.frontserver.book.ReviewSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/books/{book_id}")
public class BookDetailsController {

    private final BookClient bookClient;
    private final ReviewSummaryClient reviewSummaryClient;

    @GetMapping
    public String bookDetail(@PathVariable("book_id") Long bookId,
                             @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "20") int size,
                             @RequestHeader(name = "X-USER-ID" , required = false) String userId,
                             Model model) {
        String sort = "bookId,desc";
        BookDetailResponse bookDetail = bookClient.getBookDetail(bookId);
        PageResponse<ReviewResponse> review = bookClient.getReviews(bookId, page, size, sort);
        ReviewSummaryResponse reviewSummary = reviewSummaryClient.getReviewSummary(bookId.toString());

        model.addAttribute("book", bookDetail);
        model.addAttribute("review", review);
        model.addAttribute("reviewSummary", reviewSummary);
        if (userId != null) {
            model.addAttribute("member", userId);
        }

        return "book-detail";
    }



}
