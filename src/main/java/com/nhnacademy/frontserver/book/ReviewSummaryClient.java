package com.nhnacademy.frontserver.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "gateway-book", url = "${gateway.url}")
public interface ReviewSummaryClient {
    @GetMapping("/api/review-summary/{isbn}")
    ReviewSummaryResponse getReviewSummary(@PathVariable("isbn") String isbn);
}
