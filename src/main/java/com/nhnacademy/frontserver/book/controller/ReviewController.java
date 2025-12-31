package com.nhnacademy.frontserver.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.*;
import com.nhnacademy.frontserver.book.JsonMultipartFile;
import com.nhnacademy.frontserver.member.MemberClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewClient reviewClient;
    private final BookClient bookClient;
    private final MemberClient memberClient;
    private final ObjectMapper objectMapper;

    /**
     * 리뷰 작성 폼 이동
     */
    @GetMapping("/write")
    public String writeReviewForm(@RequestParam("bookId") Long bookId,
                                  @RequestParam("orderId") Long orderId,
                                  @RequestParam(value = "orderStatus", required = false) String orderStatus,
                                  Model model) {

        // orderStatus가 null일 수도 있으니 null 체크 추가
        if (orderStatus != null && !"CONFIRMED".equals(orderStatus)) {
            return "redirect:/my-page/orders?error=not_confirmed";
        }
        // 이미 작성된 리뷰가 있는지 확인 -> 있으면 책 상세 페이지로 Redirect
        Boolean exists = reviewClient.checkReviewExistence(orderId);
        if (exists != null && exists) {
            return "redirect:/books/" + bookId;
        }        model.addAttribute("book", bookClient.getBookDetail(bookId));
        model.addAttribute("orderId", orderId);

        return "review-form";
    }

    /**
     * 리뷰 등록 처리
     */
    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String addReview(@ModelAttribute ReviewCreateRequest request,
                            @RequestParam(value = "images", required = false) List<MultipartFile> images,
                            RedirectAttributes redirectAttributes) {
        try {
            String jsonString = objectMapper.writeValueAsString(request);
            MultipartFile jsonFile = new JsonMultipartFile(jsonString, "request");

            log.info("JSON 데이터 변환 완료: {}", jsonString);

            reviewClient.createReview(jsonFile, images);

            return "redirect:/books/" + request.bookId();
        } catch (Exception e) {
            log.error("리뷰 등록 실패", e);
          redirectAttributes.addFlashAttribute("errorMessage", "이미 리뷰를 작성하셨거나 등록 중 오류가 발생했습니다.");
            return "redirect:/books/" + request.bookId();
        }
    }

    /**
     * 마이페이지 - 내 리뷰 목록 조회
     */
    @GetMapping("/my")
    public String getMyReviews(@RequestParam(value = "page", defaultValue = "0") int page,
                               @RequestParam(value = "size", defaultValue = "10") int size,
                               Model model) {

        PageResponse<ReviewResponse> reviews = reviewClient.getMyReviews(page, size);

        model.addAttribute("reviews", reviews.content());
        model.addAttribute("activeTab", "reviews");

        return "my/my-reviews";
    }

    @PostMapping("/update")
    public String updateReview(@RequestParam("bookId") Long bookId,
                               @RequestParam("reviewId") Long reviewId,
                               @RequestParam("reviewRate") Integer reviewRate,
                               @RequestParam("reviewContents") String reviewContents,
                               RedirectAttributes redirectAttributes) {
        try {
            // DTO 생성 (이미지는 수정 불가하므로 내용과 별점만 전달)
            ReviewUpdateRequest request = new ReviewUpdateRequest(reviewRate, reviewContents);

            // Feign Client 호출
            reviewClient.updateReview(reviewId, request);

            redirectAttributes.addFlashAttribute("message", "리뷰가 수정되었습니다.");
        } catch (Exception e) {
            log.error("리뷰 수정 실패", e);
            redirectAttributes.addFlashAttribute("errorMessage", "리뷰 수정 중 오류가 발생했습니다.");
        }

        // 다시 해당 책의 상세 페이지로 이동
        return "redirect:/books/" + bookId;
    }
}