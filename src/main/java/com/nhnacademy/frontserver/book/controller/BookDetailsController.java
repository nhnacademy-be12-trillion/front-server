package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.*;
import com.nhnacademy.frontserver.member.MemberResponse;
import com.nhnacademy.frontserver.member.client.MemberClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookDetailsController {

    private final BookClient bookClient;
    private final ReviewSummaryClient reviewSummaryClient;
    private final MemberClient memberClient; // [필수] 회원 정보 조회를 위한 클라이언트

    @GetMapping("/{book_id}")
    public String bookDetail(@PathVariable("book_id") Long bookId,
                             @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "size", defaultValue = "10") int size,
                             Model model) {

        // 1. 도서 상세 정보 조회
        BookDetailResponse bookDetail = bookClient.getBookDetail(bookId);

        // 2. 리뷰 리스트 조회
        String sort = "reviewId,desc";
        PageResponse<ReviewResponse> reviewPage = bookClient.getReviews(bookId, page, size, sort);

        // [수정] 리뷰 작성자 이름 매핑 로직
        // MemberClient에 특정 ID로 타인을 조회하는 기능이 없으므로,
        // 현재 로그인한 사용자(본인)인 경우에만 이름을 갱신하고 나머지는 기존 값을 유지합니다.

        MemberResponse currentMember = null;
        try {
            // 현재 로그인한 사용자 정보 조회 (파라미터 없는 메서드 사용)
            currentMember = memberClient.getMember();
        } catch (Exception e) {
            // 비로그인 상태이거나 조회 실패 시 무시
            log.debug("비로그인 사용자입니다.");
        }

        List<ReviewResponse> updatedReviews = new ArrayList<>();
        if (reviewPage.content() != null) {
            for (ReviewResponse r : reviewPage.content()) {
                String realName;

                // 작성자 본인 확인 로직
                if (currentMember != null && r.memberId() != null &&
                        r.memberId().equals(currentMember.memberId())) {
                    // 내 리뷰라면 내 실명 사용
                    realName = currentMember.memberName();
                } else {
                    // 타인의 리뷰는 MemberClient로 이름을 조회할 수 없으므로
                    // DTO에 원래 담겨있던 writerName이나 기본값을 사용
                    // (userName 필드가 있다면 우선 사용, 없다면 writerName)
                    if (r.userName() != null && !r.userName().isEmpty()) {
                        realName = r.userName();
                    } else if (r.writerName() != null && !r.writerName().isEmpty()) {
                        realName = r.writerName();
                    } else {
                        realName = "작성자";
                    }
                }

                // 이름을 업데이트한 새로운 객체 생성 (DTO의 withWriterName 활용)
                updatedReviews.add(r.withWriterName(realName));
            }
        }

        // 업데이트된 리스트로 PageResponse 재구성
        PageResponse<ReviewResponse> finalReviewPage = new PageResponse<>(
                updatedReviews,
                reviewPage.totalPages(),
                reviewPage.totalElements(),
                reviewPage.number(),
                reviewPage.size(),
                reviewPage.first(),
                reviewPage.last()
        );

        // 3. 리뷰 요약 통계 조회
        ReviewSummaryResponse reviewSummary = null;
        try {
            reviewSummary = reviewSummaryClient.getReviewSummary(bookDetail.isbn());
        } catch (Exception e) {
            log.warn("리뷰 요약 정보를 가져올 수 없습니다.", e);
        }

        // 4. 찜 여부 확인 및 모델 데이터 설정
        boolean isLiked = false;

        if (currentMember != null) {
            // 내 리뷰 식별을 위해 현재 로그인 정보 전달
            model.addAttribute("currentMemberId", currentMember.memberId());
            model.addAttribute("currentMemberName", currentMember.memberName());

            // 찜 목록 확인
            try {
                List<BookListResponse> wishlists = bookClient.getWishlists(currentMember.memberId());
                if (wishlists != null && !wishlists.isEmpty()) {
                    isLiked = wishlists.stream()
                            .anyMatch(w -> w.bookId().equals(bookId));
                }
            } catch (Exception e) {
                log.error("위시리스트 조회 실패", e);
            }
        }

        // 5. 모델에 데이터 담기
        model.addAttribute("book", bookDetail);
        model.addAttribute("reviews", finalReviewPage); // 이름이 처리된 리뷰 목록 전달
        model.addAttribute("reviewSummary", reviewSummary);
        model.addAttribute("isLiked", isLiked);

        return "book-detail";
    }
}