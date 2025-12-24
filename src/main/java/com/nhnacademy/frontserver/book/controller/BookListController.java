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

package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import com.nhnacademy.frontserver.book.CategoryClient;
import com.nhnacademy.frontserver.book.CategoryTreeResponse; // 올려주신 DTO
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookListController {

    private final BookClient bookClient;
    private final CategoryClient categoryClient;

    @GetMapping
    public String getBooks(@RequestParam(required = false) Long categoryId,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "12") int size,
                           Model model) {

        // 1. 카테고리 트리 전체 조회 (백엔드 API 호출)
        List<CategoryTreeResponse> categoryTree = categoryClient.getCategoryTree();

        if (categoryId != null) {
            // [Case A] 특정 카테고리가 선택된 경우

            // 트리에서 현재 선택된 카테고리 노드 찾기 (재귀 탐색)
            CategoryTreeResponse currentCategory = findCategoryInTree(categoryTree, categoryId);

            if (currentCategory != null) {
                // 선택된 카테고리 이름 (예: 철학)
                model.addAttribute("currentCategoryName", currentCategory.categoryName());

                // 선택된 카테고리의 하위 카테고리 목록
                model.addAttribute("subCategories", currentCategory.children());
            } else {
                model.addAttribute("currentCategoryName", "카테고리 없음");
                model.addAttribute("subCategories", Collections.emptyList());
            }

            // 해당 카테고리의 도서 목록 조회
            PageResponse<BookListResponse> bookPage = bookClient.getBooksByCategoryId(categoryId, page, size);
            model.addAttribute("books", bookPage.content());
            model.addAttribute("page", bookPage);

        } else {
            // [Case B] 카테고리 미선택 (전체 목록)
            model.addAttribute("currentCategoryName", "도서 메뉴");
            model.addAttribute("subCategories", null); // 기본 메뉴 표시용

            // 전체 도서 조회
            PageResponse<BookListResponse> bookPage = bookClient.getBooks(page, size);
            model.addAttribute("books", bookPage.content());
            model.addAttribute("page", bookPage);
        }

        model.addAttribute("currentCategoryId", categoryId);

        return "book-list";
    }

    @GetMapping("/popularBooks")
    public String firstPageList(Model model) {
        List<BookListResponse> popularBooks = bookClient.getPopularBooks();
        model.addAttribute("books", popularBooks);
        // 인기 도서 페이지에서도 기본 사이드바 메뉴를 보여주기 위해 세팅
        model.addAttribute("currentCategoryName", "도서 메뉴");
        model.addAttribute("subCategories", null);

        return "book-list";
    }

    /**
     * 카테고리 트리(List)를 순회하며 targetId와 일치하는 카테고리 노드를 찾습니다.
     * (Depth-First Search 방식)
     */
    private CategoryTreeResponse findCategoryInTree(List<CategoryTreeResponse> nodes, Long targetId) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }

        for (CategoryTreeResponse node : nodes) {
            // 1. 현재 노드가 찾던 ID면 반환
            if (node.categoryId().equals(targetId)) {
                return node;
            }

            // 2. 자식 노드들 중에서 재귀적으로 탐색
            CategoryTreeResponse foundInChildren = findCategoryInTree(node.children(), targetId);
            if (foundInChildren != null) {
                return foundInChildren;
            }
        }
        return null;
    }
}