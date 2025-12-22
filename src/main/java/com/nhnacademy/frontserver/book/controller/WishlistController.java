package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.book.BookClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishlistController {
    private final BookClient bookClient;

    // 프론트엔드 AJAX 요청 처리
    @GetMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleWishlist(@RequestParam("bookId") Long bookId) {
        try {
            // 백엔드 호출
            Map<String, Object> result = bookClient.toggleWishlist(bookId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 로그인 안 된 경우 등 예외 처리
            return ResponseEntity.status(401)
                    .body(Map.of("success", false, "message", "로그인이 필요한 서비스입니다."));
        }
    }
}
