package com.nhnacademy.frontserver.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "reviewSummaryClient", url = "https://localhost:10416")
public interface ReviewSummaryClient {
    @GetMapping("/api/books/{isbn}/review-summary")
    ReviewSummaryResponse getReviewSummary(@PathVariable("isbn") String isbn);
}
