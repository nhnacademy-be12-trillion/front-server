package com.nhnacademy.frontserver.book.controller;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.MemberResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/wishlists")
public class WishlistController {

    private final BookClient bookClient;
    private final MemberClient memberClient;

    @GetMapping
    public String wishlistsPage(Model model) {
        try {
            MemberResponse memberResponse = memberClient.getMember();
            if(Objects.nonNull(memberResponse)) {
                // [수정] 회원 ID를 헤더로 전달
                List<BookListResponse> wishlists = bookClient.getWishlists(memberResponse.memberId());
                model.addAttribute("books", wishlists);
                return "wishlist";
            }
            return "redirect:/login";
        } catch (Exception e) {
            log.error("위시리스트 페이지 조회 실패: ", e);
            return "redirect:/login";
        }
    }

    @GetMapping("/toggle")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggle(@RequestParam("bookId") Long bookId) {
        try {
            MemberResponse memberResponse = memberClient.getMember();
            if(Objects.nonNull(memberResponse)) {
                // [수정] 실제 로그인한 사용자의 ID를 백엔드에 명시적 전달
                Map<String, Object> result = bookClient.toggleWishlist(bookId, memberResponse.memberId());
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        } catch (FeignException e) {
            log.error("백엔드 통신 에러 (상태: {}): {}", e.status(), e.getMessage());
            return ResponseEntity.status(e.status()).body(Map.of("success", false, "message", "백엔드 서버 오류"));
        } catch (Exception e) {
            log.error("찜하기 시스템 에러: ", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "알 수 없는 오류 발생"));
        }
    }

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Integer> getWishlistCount() {
        try {
            MemberResponse memberResponse = memberClient.getMember();
            if(Objects.nonNull(memberResponse)) {
                List<BookListResponse> wishlists = bookClient.getWishlists(memberResponse.memberId());
                return ResponseEntity.ok(wishlists.size());
            }
            return ResponseEntity.ok(0);
        } catch (Exception e) {
            return ResponseEntity.ok(0);
        }
    }
}