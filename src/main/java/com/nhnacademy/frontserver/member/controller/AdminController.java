package com.nhnacademy.frontserver.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookCreateRequest;
import com.nhnacademy.frontserver.book.CategorySearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin") // [핵심] 브라우저는 /admin 경로로 접근 (Gateway의 /api/admin과 분리)
@RequiredArgsConstructor
public class AdminController {

    private final BookClient bookClient;
    private final ObjectMapper objectMapper;

    // =================================================================================
    // 1. 관리자 페이지 화면 (View) - GET /admin
    // =================================================================================
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("orders", Collections.emptyList());
        model.addAttribute("shippingPolicy", Map.of(
                "shippingFee", 3000,
                "freeShippingCondition", 50000
        ));
        model.addAttribute("packagingList", Collections.emptyList());
        model.addAttribute("pointPolicy", Map.of(
                "basePointRate", 1.0
        ));
        model.addAttribute("memberGrades", Collections.emptyList());
        model.addAttribute("memberInfo", null);

        return "admin";
    }

    // =================================================================================
    // 2. 도서 등록 관련 API (Action)
    // =================================================================================

    /**
     * 도서 등록 요청 처리 (POST /admin/books)
     * HTML Form action="@{/admin/books}"
     */
    @PostMapping("/books")
    public String createBook(
            @ModelAttribute BookCreateRequest request,
            @RequestParam(value = "bookImageFile", required = false) MultipartFile file
    ) {
        try {
            log.info(">>>> [프론트] 도서 등록 요청 시작: title={}, isbn={}", request.getBookName(), request.getIsbn());

            // 1. DTO -> JSON String 변환
            String jsonRequest = objectMapper.writeValueAsString(request);

            // 2. JSON Bytes를 담은 커스텀 MultipartFile 생성
            MultipartFile jsonPart = new DtoMultipartFile(
                    "book",
                    "book.json",
                    "application/json",
                    jsonRequest.getBytes(StandardCharsets.UTF_8)
            );

            // 3. Client 호출 -> Gateway(/api/admin/books)로 전달
            bookClient.createBook(jsonPart, file);
            log.info(">>>> [프런트] 도서 등록 성공");

            return "redirect:/admin?success=true";
        } catch (Exception e) {
            log.error(">>>> [프런트] 도서 등록 실패", e);
            return "redirect:/admin?error=create_failed";
        }
    }

    /**
     * ISBN 검색 (GET /admin/books/isbn/{isbn})
     * JS fetch: /admin/books/isbn/...
     */
    @GetMapping("/books/isbn/{isbn}")
    @ResponseBody
    public ResponseEntity<BookCreateRequest> getBookInfoByIsbn(@PathVariable("isbn") String isbn) {
        try {
            log.info(">>>> [프런트] ISBN 검색 요청 수신: {}", isbn);
            // 실제 데이터는 Feign Client가 Gateway를 통해 가져옴
            BookCreateRequest bookInfo = bookClient.getBookInfoByIsbn(isbn);
            log.info(">>>> [프런트] ISBN 데이터 수신 완료: {}", bookInfo.getBookName());
            return ResponseEntity.ok(bookInfo);
        } catch (Exception e) {
            log.error(">>>> [프런트] ISBN 검색 실패 (Gateway/Backend 통신 오류): {}", isbn, e);
            // 502를 리턴하면 HTML JS에서 catch로 잡음
            return ResponseEntity.status(502).build();
        }
    }

    /**
     * 카테고리 검색 (GET /admin/categories/search)
     * JS fetch: /admin/categories/search?keyword=...
     */
    @GetMapping("/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchResponse>> searchCategories(@RequestParam("keyword") String keyword) {
        try {
            log.info(">>>> [프런트] 카테고리 검색 요청: {}", keyword);
            List<CategorySearchResponse> categories = bookClient.searchCategories(keyword);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error(">>>> [프런트] 카테고리 검색 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Helper Class for Multipart
    private static class DtoMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public DtoMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }
        @Override public String getName() { return name; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return content == null || content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() throws IOException { return content; }
        @Override public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(content); }
        @Override public void transferTo(File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("TransferTo not supported");
        }
    }
}