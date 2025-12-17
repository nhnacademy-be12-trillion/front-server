/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2025. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
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
        PageResponse<BookListResponse> books = bookClient.getBooks();
        model.addAttribute("books", books);


        // 인기 도서 (조회수 기준) -> "지금 많이 보고 있는 책"
        List<BookListResponse> popularBooks = bookClient.getPopularBooks();
        model.addAttribute("popularBooks", popularBooks);

        // 3. [추가] 1차 카테고리(루트) 목록 조회 (버튼용)
        List<CategoryTreeResponse> rootCategories = bookClient.getRootCategories();
        model.addAttribute("rootCategories", rootCategories);

        // 4. [변경] 선택된 카테고리의 추천 도서 조회
        List<BookListResponse> categoryBooks = bookClient.getBooksByCategory(categoryId);
        model.addAttribute("categoryBooks", categoryBooks);

        // 5. 화면 표시용 데이터 (선택된 ID 및 이름)
        model.addAttribute("selectedCategoryId", categoryId);

        String selectedCategoryName = rootCategories.stream()
                .filter(c -> c.categoryId().equals(categoryId))
                .findFirst()
                .map(CategoryTreeResponse::categoryName)
                .orElse("추천");
        model.addAttribute("selectedCategoryName", selectedCategoryName);

        return "index";
    }
}
