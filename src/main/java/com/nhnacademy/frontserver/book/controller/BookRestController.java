package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books") // index.html의 AJAX 요청 경로와 일치
public class BookRestController {

    private final BookClient bookClient;

    // [AJAX] 전체 신간 도서 조회 프록시
    // index.html에서 fetch('/books/new-books')라고 호출하면 이 메서드가 실행됨
    @GetMapping("/new-books")
    public ResponseEntity<List<BookListResponse>> getNewBooks() {
        // Feign Client를 통해 백엔드 API 호출 -> 결과 반환
        return ResponseEntity.ok(bookClient.getNewBooks());
    }

    // [AJAX] 카테고리별 신간 도서 조회 프록시
    // index.html에서 fetch('/books/categories/{id}/top')라고 호출하면 이 메서드가 실행됨
    @GetMapping("/categories/{categoryId}/top")
    public ResponseEntity<List<BookListResponse>> getBooksByCategory(@PathVariable("categoryId") Long categoryId) {
        return ResponseEntity.ok(bookClient.getBooksByCategory(categoryId));
    }
}