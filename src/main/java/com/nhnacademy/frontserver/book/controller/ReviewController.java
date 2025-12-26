package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.book.ReviewRequest;
import com.nhnacademy.frontserver.member.MemberClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final BookClient bookClient;
    private final MemberClient memberClient;

    // 리뷰 작성 폼으로 이동 (주문 상세에서 호출)
    @GetMapping("/write")
    public String writeReviewForm(@RequestParam("bookId") Long bookId,
                                  @RequestParam("orderStatus") String orderStatus,
                                  Model model) {
        // 주문 확정 상태 확인 (주문 서비스의 상태값에 맞춰 비교)
        if (!"CONFIRM".equals(orderStatus)) {
            return "redirect:/mypage/orders?error=not_confirmed";
        }

        BookDetailResponse book = bookClient.getBookDetail(bookId);
        model.addAttribute("book", book);
        return "review-form"; // review-form.html 필요
    }

    // 리뷰 등록 처리
    @PostMapping("/add")
    public String addReview(@ModelAttribute ReviewRequest request,
                            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            memberClient.getMember(); // 로그인 확인
            bookClient.createReview(request, images); // 백엔드 호출 (포인트 적립 로직 포함)
            return "redirect:/books/" + request.bookId(); // 성공 시 도서 상세로
        } catch (Exception e) {
            return "redirect:/login";
        }
    }
}