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

import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import com.nhnacademy.frontserver.PageResponse;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "gateway-order",
        url = "${gateway.url}")
public interface BookClient {

    // 도서 목록 조회
    @GetMapping("/api/books")
    PageResponse<BookListResponse> getBooks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "bookId,desc") String sort
    );

    default PageResponse<BookListResponse> getBooks() {
        return getBooks(0, 20, "bookId,desc");
    }

    @GetMapping("/api/books/{book_id}")
    BookDetailResponse getBookDetail(@PathVariable("book_id") Long bookId);

    @GetMapping("/api/books/{book_id}/reviews")
    PageResponse<ReviewResponse> getReviews(@PathVariable("book_id") Long bookId,
                                            @RequestParam("page") int page,
                                            @RequestParam("size") int size,
                                            @RequestParam("sort") String sort);

    @GetMapping("/api/books/categories")
    List<CategoryTreeResponse> getCategoryTree();

    @GetMapping("/api/books/reviews/me")
    PageResponse<ReviewResponse> getReviewsMe(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam("sort") String sort);

    // 인기 도서 (조회수 기준) Top 5
    @GetMapping("/api/books/popular-books")
    List<BookListResponse> getPopularBooks();

    // 카테고리별 도서 Top 5
    @GetMapping("/api/books/categories/{categoryId}/top")
    List<BookListResponse> getBooksByCategory(@PathVariable("categoryId") Long categoryId);

    // 뎁스1 카테고리 목록 조회 (메인 페이지 버튼)
    @GetMapping("/api/books/categories/roots")
    List<CategoryTreeResponse> getRootCategories();

    @GetMapping("api/books/best-sellers")
    List<BookListResponse> getBestSellers();

    // 찜하기 토글 (백엔드의 POST /wishlists/{book-id} 호출)
    @PostMapping("/wishlists/{bookId}")
    Map<String, Object> toggleWishlist(@PathVariable("bookId") Long bookId);
}