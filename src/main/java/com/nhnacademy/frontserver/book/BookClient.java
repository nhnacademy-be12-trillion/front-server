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

package com.nhnacademy.frontserver.book;

import com.nhnacademy.frontserver.PageResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "book-service", url = "http://localhost:10413")
public interface BookClient {

    //TODO 현재 publisherName 호출 시 null
    // Pageable 응답 예시
    @GetMapping("/api/books")
    PageResponse<BookListResponse> getBooks(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sort
    );

    @GetMapping("/api/books/{book_id}")
    BookDetailResponse getBookDetail(@PathVariable("book_id") Long bookId);

    @GetMapping("/api/books/{book_id}/reviews")
    PageResponse<ReviewResponse> getReviews(@PathVariable("book_id") Long bookId,
                                            @RequestParam("page") int page,
                                            @RequestParam("size") int size,
                                            @RequestParam("sort") String sort);

    @GetMapping("/api/categories")
    List<CategoryTreeResponse> getCategoryTree();

    @GetMapping("/api/books/reviews/me")
    PageResponse<ReviewResponse> getReviewsMe(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam("sort") String sort);
}
