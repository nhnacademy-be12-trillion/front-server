package com.nhnacademy.frontserver.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "gateway-review-summary",
        url = "${gateway.url}")
public interface ReviewSummaryClient {
    @GetMapping("/api/books/{isbn}/review-summary")
    ReviewSummaryResponse getReviewSummary(@PathVariable("isbn") String isbn);
}
