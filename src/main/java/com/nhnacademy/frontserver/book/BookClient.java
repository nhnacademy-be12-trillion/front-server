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
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@FeignClient(name = "gateway-order",
        url = "${gateway.url}")
public interface BookClient {

    // 도서 목록 조회 (전체)
    @GetMapping("/api/books")
    PageResponse<BookListResponse> getBooks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "bookId,desc") String sort
    );

    default PageResponse<BookListResponse> getBooks(int page, int size) {
        return getBooks(page, size, "bookId,desc");
    }

    default PageResponse<BookListResponse> getBooks() {
        return getBooks(0, 20, "bookId,desc");
    }

    // 카테고리별 도서 목록 조회 (페이징 포함)
    @GetMapping("/api/books/categories/{categoryId}")
    PageResponse<BookListResponse> getBooksByCategoryId(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "bookId,desc") String sort
    );

    // 편의상 오버로딩 (sort 생략 시 기본값 사용)
    default PageResponse<BookListResponse> getBooksByCategoryId(Long categoryId, int page, int size) {
        return getBooksByCategoryId(categoryId, page, size, "bookId,desc");
    }

    @GetMapping("/api/books/{book_id}")
    BookDetailResponse getBookDetail(@PathVariable("book_id") Long bookId);

    // 리뷰 등록 API
    @PostMapping(value = "/api/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Long createReview(
            @RequestPart("request") ReviewRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    );

    // 특정 도서의 리뷰 목록 조회
    @GetMapping("/api/books/{book_id}/reviews")
    PageResponse<ReviewResponse> getReviews(@PathVariable("book_id") Long bookId,
                                            @RequestParam("page") int page,
                                            @RequestParam("size") int size,
                                            @RequestParam("sort") String sort);

    // 카테고리 트리 조회
    @GetMapping("/api/books/categories")
    List<CategoryTreeResponse> getCategoryTree();

    @GetMapping("/api/books/reviews/me")
    PageResponse<ReviewResponse> getReviewsMe(@RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam("sort") String sort);

    // 인기 도서 (조회수 기준) Top 10
    @GetMapping("/api/books/popular-books")
    List<BookListResponse> getPopularBooks();

    // 전체 신간 도서 Top 5
    @GetMapping("/api/books/new-books")
    List<BookListResponse> getNewBooks();

    // 카테고리별 도서 Top 5 (메인페이지 등에서 간단히 보여줄 때 사용)
    @GetMapping("/api/books/categories/{categoryId}/top")
    List<BookListResponse> getBooksByCategory(@PathVariable("categoryId") Long categoryId);

    // 뎁스1 카테고리 목록 조회 (메인 페이지 버튼)
    @GetMapping("/api/books/categories/roots")
    List<CategoryTreeResponse> getRootCategories();

    @GetMapping("/api/books/best-sellers")
    List<BookListResponse> getBestSellers();

    // 찜하기 토글
    @PostMapping("/api/books/wishlists/{book-id}")
    Map<String, Object> toggleWishlist(
            @PathVariable("book-id") Long bookId,
            @RequestHeader("X-Member-Id") Long memberId
    );

    // 위시리스트 목록 조회 (페이지용 + 뱃지 카운트용)
    @GetMapping("/api/books/wishlists")
    List<BookListResponse> getWishlists(
            @RequestHeader("X-Member-Id") Long memberId
    );

    @PostMapping(value = "/api/admin/books", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Long createBook(
            @RequestPart("book") MultipartFile bookJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    );
     // ISBN으로 도서 정보 조회 (AI/알라딘)
    @GetMapping("/api/admin/books/isbn/{isbn}")
    BookCreateRequest getBookInfoByIsbn(@PathVariable("isbn") String isbn);

    // [관리자] 도서 수정 (추가됨)
    @PutMapping("/api/admin/books/{bookId}")
    void updateBook(@PathVariable("bookId") Long bookId, @RequestBody Map<String, Object> request);

    // [관리자] 도서 삭제 (추가됨)
    @DeleteMapping("/api/admin/books/{bookId}")
    void deleteBook(@PathVariable("bookId") Long bookId);

    @GetMapping("/api/admin/categories/search")
    List<CategorySearchResponse> searchCategories(@RequestParam("keyword") String keyword);


}