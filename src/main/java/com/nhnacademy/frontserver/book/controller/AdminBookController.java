package com.nhnacademy.frontserver.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookCreateRequest;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.book.BookState;
import com.nhnacademy.frontserver.search.SearchClient;
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
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/books") // 기본 경로 설정
public class AdminBookController {

    private final BookClient bookClient;
    private final ObjectMapper objectMapper; // JSON 변환용
    private final SearchClient searchClient; // 검색 서버 호출

    // =========================================================================
    // 1. 화면(View) 반환 - 경로 수정됨 (admin/ 접두어 추가)
    // =========================================================================

    // 도서 관리(수정/삭제) 페이지
    @GetMapping("/management")
    public String bookManagementPage(Model model) {
        model.addAttribute("activeMenu", "book-management");
        // [수정] 템플릿 경로를 "admin/book-management"로 변경하여 500 에러 해결
        return "admin/book-management";
    }

    // 신규 도서 등록 페이지
    @GetMapping("/register")
    public String bookRegisterPage(Model model) {
        model.addAttribute("activeMenu", "book-register");
        // [수정] 템플릿 경로를 "admin/book-register"로 변경
        return "admin/book-register";
    }

    // =========================================================================
    // 2. 도서 등록 및 정보 조회 (AdminController에서 가져옴)
    // =========================================================================

    // 도서 등록 처리 (POST /admin/books)
    @PostMapping
    public String createBook(
            @ModelAttribute BookCreateRequest request,
            @RequestParam(value = "bookImageFile", required = false) MultipartFile file
    ) {
        try {
            if (request.getBookState() == null) request.setBookState(BookState.ON_SALE);
            if (request.getBookPackaging() == null) request.setBookPackaging(true);

            log.info(">>>> [Admin] 도서 등록 요청: {}", request.getBookName());

            String jsonRequest = objectMapper.writeValueAsString(request);
            MultipartFile jsonPart = new DtoMultipartFile(
                    "book", "book.json", "application/json",
                    jsonRequest.getBytes(StandardCharsets.UTF_8)
            );

            // 1) Book 서비스(DB) 등록
            bookClient.createBook(jsonPart, file);

            // 2) Search 서비스(ES) 신규 인덱싱 트리거: ISBN 기반
            try {
                String isbn = (request.getIsbn() == null) ? null : request.getIsbn().trim();
                if (isbn != null && !isbn.isBlank()) {
                    log.info(">>>> [Admin] Search 인덱싱 요청(신규): isbn={}", isbn);
                    searchClient.upsertByIsbn(isbn);
                } else {
                    log.warn(">>>> [Admin] Search 인덱싱 스킵: isbn 비어있음");
                }
            } catch (Exception e) {
                // DB 등록 성공했는데 ES 인덱싱 실패 -> 운영상 흔함(지연/네트워크) -> 로그만
                log.error(">>>> [Admin] Search 인덱싱 실패(신규): isbn={}", request.getIsbn(), e);
            }

            // 성공 시 다시 등록 페이지로 리다이렉트 (혹은 목록으로)
            return "redirect:/admin/books/register?success=true";
        } catch (Exception e) {
            log.error(">>>> [Admin] 도서 등록 실패", e);
            return "redirect:/admin/books/register?error=create_failed";
        }
    }

    // ISBN으로 도서 정보 조회 (AI/알라딘)
    @GetMapping("/isbn/{isbn}")
    @ResponseBody
    public ResponseEntity<BookCreateRequest> getBookInfoByIsbn(@PathVariable("isbn") String isbn) {
        try {
            return ResponseEntity.ok(bookClient.getBookInfoByIsbn(isbn));
        } catch (Exception e) {
            return ResponseEntity.status(502).build();
        }
    }

    // =========================================================================
    // 3. 도서 수정/삭제 API (AJAX)
    // =========================================================================

    // 도서 상세 조회
    @GetMapping("/{bookId}/detail")
    @ResponseBody
    public ResponseEntity<?> getBookDetail(@PathVariable("bookId") Long bookId) {
        try {
            BookDetailResponse book = bookClient.getBookDetail(bookId);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            return ResponseEntity.status(404).body("도서를 찾을 수 없습니다.");
        }
    }

    // 도서 수정
    @PutMapping("/{bookId}")
    @ResponseBody
    public ResponseEntity<String> updateBook(
            @PathVariable("bookId") Long bookId,
            @RequestBody Map<String, Object> request
    ) {
        try {
            log.info(">>>> [Admin] 도서 수정 요청: ID={}", bookId);

            // 1) Book 서비스(DB) 수정
            bookClient.updateBook(bookId, request);

            // 2) Search 서비스(ES) 갱신 트리거: bookId 기반
            try {
                log.info(">>>> [Admin] Search 인덱싱 요청(갱신): bookId={}", bookId);
                searchClient.upsertByBookId(bookId);
            } catch (Exception e) {
                log.error(">>>> [Admin] Search 인덱싱 실패(갱신): bookId={}", bookId, e);
            }

            return ResponseEntity.ok("도서가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("도서 수정 실패", e);
            return ResponseEntity.status(500).body("수정 실패: " + e.getMessage());
        }
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    @ResponseBody
    public ResponseEntity<String> deleteBook(@PathVariable("bookId") Long bookId) {
        try {
            log.info(">>>> [Admin] 도서 삭제 요청: ID={}", bookId);

            // 1) Book 서비스(DB) 삭제
            bookClient.deleteBook(bookId);

            // 2) Search 서비스(ES) 삭제 트리거: bookId 기반
            try {
                log.info(">>>> [Admin] Search 인덱스 삭제 요청: bookId={}", bookId);
                searchClient.deleteByBookId(bookId);
            } catch (Exception e) {
                log.error(">>>> [Admin] Search 인덱스 삭제 실패: bookId={}", bookId, e);
            }

            return ResponseEntity.ok("도서가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("도서 삭제 실패", e);
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }

    // MultipartFile 구현체 (내부 클래스)
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
        @Override public void transferTo(File dest) throws IOException, IllegalStateException { throw new UnsupportedOperationException(); }
    }
}