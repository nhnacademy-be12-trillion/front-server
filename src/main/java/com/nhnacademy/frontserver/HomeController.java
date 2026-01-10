package com.nhnacademy.frontserver;

import com.nhnacademy.frontserver.auth.util.AuthHelper;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import com.nhnacademy.frontserver.member.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

    private final BookClient bookClient;
    private final AuthHelper authHelper;

    @GetMapping
    public String home(
            @RequestParam(name = "categoryId", defaultValue = "0") Long categoryId,
            Model model
    ) {
        // 카테고리 / 인기 도서 / 신간 / 베스트셀러

        // 뎁스1 카테고리 목록
        List<CategoryTreeResponse> rootCategories = Collections.emptyList();
        try {
            rootCategories = bookClient.getRootCategories();
        } catch (Exception e) {
            log.error("카테고리 조회 실패", e);
        }
        model.addAttribute("rootCategories", rootCategories);

        // 인기 도서 (Top 10)
        try {
            List<BookListResponse> popularBooks = bookClient.getPopularBooks();
            model.addAttribute("popularBooks", popularBooks);
        } catch (Exception e) {
            model.addAttribute("popularBooks", Collections.emptyList());
        }

        // 신간 도서 조회 (전체 vs 카테고리별)
        List<BookListResponse> categoryBooks = Collections.emptyList();
        String selectedCategoryName = "전체";

        try {
            if (categoryId == 0L) {
                categoryBooks = bookClient.getNewBooks();
                selectedCategoryName = "전체";
            } else {
                categoryBooks = bookClient.getBooksByCategory(categoryId);
                // 카테고리 이름 찾기
                if (rootCategories != null) {
                    Long finalCategoryId = categoryId;
                    selectedCategoryName = rootCategories.stream()
                            .filter(c -> c.categoryId().equals(finalCategoryId))
                            .findFirst()
                            .map(CategoryTreeResponse::categoryName)
                            .orElse("카테고리");
                }
            }
        } catch (Exception e) {
            log.error("신간 도서 조회 실패", e);
        }
        model.addAttribute("categoryBooks", categoryBooks);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedCategoryName", selectedCategoryName);

        // 베스트셀러
        List<BookListResponse> bestSellers = Collections.emptyList();
        try {
            bestSellers = bookClient.getBestSellers();
        } catch (Exception e) {
            log.info("베스트셀러 조회 실패: {}", e.getMessage());
        }
        model.addAttribute("bestSellers", bestSellers);

        // 로그인한 사용자의 찜 목록 ID 가져오기 (하트 색칠용)
        List<Long> wishlistBookIds = new ArrayList<>();

        try {
            // AuthHelper로 로그인 회원 정보 조회
            MemberResponse member = authHelper.getMember();

            if (member != null) {
                Long memberId = member.memberId();
                // 찜 목록 API 호출
                List<BookListResponse> myWishlist = bookClient.getWishlists(memberId);

                if (myWishlist != null) {
                    // 책 목록에서 ID만 추출
                    wishlistBookIds = myWishlist.stream()
                            .map(BookListResponse::bookId)
                            .collect(Collectors.toList());
                }
            }
        } catch (Exception e) {
            log.debug("위시리스트 로딩 실패(비회원 등): {}", e.getMessage());
        }

        model.addAttribute("wishlistBookIds", wishlistBookIds);

        return "index";
    }
}