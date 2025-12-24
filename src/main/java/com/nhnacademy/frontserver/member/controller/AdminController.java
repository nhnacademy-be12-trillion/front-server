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
@RequiredArgsConstructor
public class AdminController {

    private final BookClient bookClient;
    private final ObjectMapper objectMapper; // JSON 변환용

    // =================================================================================
    // 1. 관리자 페이지 화면 (View) - GET /admin
    // =================================================================================
    @GetMapping("/admin")
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
     * 도서 등록 요청 처리 (POST /api/admin/books)
     * Feign Multipart 전송 시 DTO 파트 누락 문제를 해결하기 위해 DTO를 JSON 파일로 변환하여 전송
     */
    @PostMapping("/api/admin/books")
    public String createBook(
            @ModelAttribute BookCreateRequest request,
            @RequestParam(value = "bookImageFile", required = false) MultipartFile file
    ) {
        try {
            log.info("도서 등록 요청: title={}, isbn={}", request.getBookName(), request.getIsbn());

            // 1. DTO -> JSON String -> Bytes 변환
            String jsonRequest = objectMapper.writeValueAsString(request);

            // 2. JSON Bytes를 담은 커스텀 MultipartFile 생성 (Content-Type: application/json)
            MultipartFile jsonPart = new DtoMultipartFile(
                    "book",
                    "book.json",
                    "application/json",
                    jsonRequest.getBytes(StandardCharsets.UTF_8)
            );

            // 3. Client 호출 (JSON Part + Image File Part)
            bookClient.createBook(jsonPart, file);

            return "redirect:/admin";
        } catch (Exception e) {
            log.error("도서 등록 실패", e);
            return "redirect:/admin?error=create_failed";
        }
    }

    @GetMapping("/api/admin/books/isbn/{isbn}")
    @ResponseBody
    public ResponseEntity<BookCreateRequest> getBookInfoByIsbn(@PathVariable("isbn") String isbn) {
        try {
            log.info("ISBN 검색 요청: {}", isbn);
            BookCreateRequest bookInfo = bookClient.getBookInfoByIsbn(isbn);
            return ResponseEntity.ok(bookInfo);
        } catch (Exception e) {
            log.warn("ISBN 검색 실패: {}", isbn);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/admin/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchResponse>> searchCategories(@RequestParam("keyword") String keyword) {
        try {
            List<CategorySearchResponse> categories = bookClient.searchCategories(keyword);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("카테고리 검색 실패", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * DTO를 MultipartFile로 포장하기 위한 내부 클래스
     */
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

        @Override
        public String getName() { return name; }

        @Override
        public String getOriginalFilename() { return originalFilename; }

        @Override
        public String getContentType() { return contentType; }

        @Override
        public boolean isEmpty() { return content == null || content.length == 0; }

        @Override
        public long getSize() { return content.length; }

        @Override
        public byte[] getBytes() throws IOException { return content; }

        @Override
        public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(content); }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("TransferTo not supported for DTO wrapper");
        }
    }
}