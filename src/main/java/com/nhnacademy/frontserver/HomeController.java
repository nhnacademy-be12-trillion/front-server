/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2025. NHN Academy Corp. All rights reserved.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.frontserver;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

    private final BookClient bookClient;

    @GetMapping
    public String home(
            @RequestParam(name = "categoryId", defaultValue = "1") Long categoryId,
            Model model
    ) {
        // 전체 도서 목록
        try {
            PageResponse<BookListResponse> books = bookClient.getBooks();
            model.addAttribute("books", books);
        } catch (Exception e) {
            model.addAttribute("books", null);
        }

        // 인기 도서
        try {
            List<BookListResponse> popularBooks = bookClient.getPopularBooks();
            model.addAttribute("popularBooks", popularBooks);
        } catch (Exception e) {
            model.addAttribute("popularBooks", Collections.emptyList());
        }

        // 뎁스1 카테고리 목록 조회
        List<CategoryTreeResponse> rootCategories = Collections.emptyList();
        try {
            rootCategories = bookClient.getRootCategories();
        } catch (Exception e) {
            System.err.println("카테고리 조회 실패: " + e.getMessage());
        }
        model.addAttribute("rootCategories", rootCategories);

        // 신간 도서 조회 (전체 vs 카테고리별)
        List<BookListResponse> categoryBooks = Collections.emptyList();
        String selectedCategoryName = "전체";

        try {
            if (categoryId == 0L) {
                // '전체' 선택 시 -> 전체 신간 Top 5 호출
                categoryBooks = bookClient.getNewBooks();
                selectedCategoryName = "전체";
            } else {
                // '카테고리' 선택 시 -> 해당 카테고리 신간 Top 5 호출
                categoryBooks = bookClient.getBooksByCategory(categoryId);

                // 카테고리 이름 찾기
                if (rootCategories != null) {
                    selectedCategoryName = rootCategories.stream()
                            .filter(c -> c.categoryId().equals(categoryId))
                            .findFirst()
                            .map(CategoryTreeResponse::categoryName)
                            .orElse("카테고리");
                }
            }
        } catch (Exception e) {
            System.err.println("추천 도서 조회 실패: " + e.getMessage());
        }

        model.addAttribute("categoryBooks", categoryBooks);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedCategoryName", selectedCategoryName);

        // 베스트셀러 도서 조회 (Top 5)
        List<BookListResponse> bestSellers = Collections.emptyList();
        try {
            bestSellers = bookClient.getBestSellers();
        } catch (Exception e) {
            System.err.println("베스트셀러 조회 실패: " + e.getMessage());
        }
        model.addAttribute("bestSellers", bestSellers);

        return "index";
    }
}