package com.nhnacademy.frontserver.controller;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishlistController {

    private final BookClient bookClient;

    // 1. 위시리스트 페이지 조회 (화면)
    @GetMapping
    public String wishlistsPage(Model model) {
        try {
            List<BookListResponse> wishlists = bookClient.getWishlists();
            model.addAttribute("books", wishlists);
            return "wishlist"; // wishlist.html 반환
        } catch (Exception e) {
            // 로그인 안 된 경우 등 -> 로그인 페이지로
            return "redirect:/auth/login";
        }
    }

    // 2. 찜 토글 (AJAX)
    @GetMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggle(@RequestParam("bookId") Long bookId) {
        try {
            Map<String, Object> result = bookClient.toggleWishlist(bookId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
    }

    // 3. 찜 개수 조회 (AJAX - 배지 업데이트용)
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Integer> getWishlistCount() {
        try {
            List<BookListResponse> wishlists = bookClient.getWishlists();
            return ResponseEntity.ok(wishlists.size());
        } catch (Exception e) {
            return ResponseEntity.ok(0); // 에러나면 0개로 표시
        }
    }
}