package com.nhnacademy.frontserver.book;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.auth.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "gateway-book", url = "${gateway.url}", contextId = "reviewClient", configuration = FeignClientConfig.class)
public interface ReviewClient {

    /**
     * 리뷰 등록
     */
    @PostMapping(value = "/api/books/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Long createReview(
            @RequestPart("request") MultipartFile request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    );

    @GetMapping("/api/books/{bookId}/reviews")
    PageResponse<ReviewResponse> getReviewsByBookId(@PathVariable("bookId") Long bookId,
                                                    @RequestParam("page") int page,
                                                    @RequestParam("size") int size);

    @GetMapping("/api/books/reviews/me")
    PageResponse<ReviewResponse> getMyReviews(@RequestParam("page") int page,
                                              @RequestParam("size") int size);

    // 주문 ID로 리뷰 존재 여부 확인
    @GetMapping("/api/books/reviews/check/{orderId}")
    Boolean checkReviewExistence(@PathVariable("orderId") Long orderId);
}