package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.book.ReviewResponse;
import com.nhnacademy.frontserver.book.ReviewSummaryClient;
import com.nhnacademy.frontserver.book.ReviewSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books") // 기본 경로 분리
public class BookDetailsController {

    private final BookClient bookClient;
    private final ReviewSummaryClient reviewSummaryClient;

    // 상세 페이지 매핑: /books/{book_id}
    @GetMapping("/{book_id}")
    public String bookDetail(@PathVariable("book_id") Long bookId,
                             @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "10") int size, // 리뷰는 보통 10개씩
                             Model model) {

        // 1. 도서 상세 정보 조회
        BookDetailResponse bookDetail = bookClient.getBookDetail(bookId);

        // 2. 리뷰 리스트 조회 (정렬 기준은 최신순 등 상황에 맞게)
        String sort = "reviewId,desc";
        PageResponse<ReviewResponse> review = bookClient.getReviews(bookId, page, size, sort);

        // 3. 리뷰 요약 통계 조회 (에러 방지를 위해 try-catch 혹은 null 처리 권장)
        ReviewSummaryResponse reviewSummary = null;
        try {
            reviewSummary = reviewSummaryClient.getReviewSummary(bookId.toString());
        } catch (Exception e) {
            log.warn("리뷰 요약 정보를 가져올 수 없습니다.", e);
        }

        model.addAttribute("book", bookDetail);           // 단일 객체
        model.addAttribute("reviews", review);            // 리뷰 페이징 객체 (변수명 복수형 권장)
        model.addAttribute("reviewSummary", reviewSummary);

        return "book-detail";
    }
}