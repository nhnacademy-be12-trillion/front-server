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
        // 1. 전체 도서 목록
        try {
            PageResponse<BookListResponse> books = bookClient.getBooks();
            model.addAttribute("books", books);
        } catch (Exception e) {
            model.addAttribute("books", null);
        }

        // 2. 인기 도서
        try {
            List<BookListResponse> popularBooks = bookClient.getPopularBooks();
            model.addAttribute("popularBooks", popularBooks);
        } catch (Exception e) {
            model.addAttribute("popularBooks", Collections.emptyList());
        }

        // 3. [안전] 1차 카테고리 목록 조회
        List<CategoryTreeResponse> rootCategories = Collections.emptyList();
        try {
            rootCategories = bookClient.getRootCategories();
        } catch (Exception e) {
            System.err.println("카테고리 조회 실패: " + e.getMessage());
        }
        model.addAttribute("rootCategories", rootCategories);

        // 4. [안전] 선택된 카테고리 도서 조회
        List<BookListResponse> categoryBooks = Collections.emptyList();
        try {
            categoryBooks = bookClient.getBooksByCategory(categoryId);
        } catch (Exception e) {
            System.err.println("카테고리 도서 조회 실패: " + e.getMessage());
        }
        model.addAttribute("categoryBooks", categoryBooks);

        // 5. 카테고리 이름 찾기 (Null 방어 로직 적용)
        model.addAttribute("selectedCategoryId", categoryId);

        String selectedCategoryName = "추천";
        if (rootCategories != null && !rootCategories.isEmpty()) {
            selectedCategoryName = rootCategories.stream()
                    .filter(c -> c.categoryId().equals(categoryId))
                    .findFirst()
                    .map(CategoryTreeResponse::categoryName)
                    .orElse("추천");
        }
        model.addAttribute("selectedCategoryName", selectedCategoryName);

        return "index";
    }
}