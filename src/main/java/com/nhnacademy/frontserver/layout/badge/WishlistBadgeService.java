package com.nhnacademy.frontserver.layout.badge;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.member.MemberResponse;
import com.nhnacademy.frontserver.member.client.MemberClient;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistBadgeService {

    private final MemberClient memberClient;
    private final BookClient bookClient;

    // 위시리스트 개수 조회: 로그인 쿠키(accessToken)가 없으면 0 반환
    public int getWishlistCount() {
        if (!hasAccessToken()) {
            return 0;
        }
        try {
            // 회원 정보 조회
            MemberResponse member = memberClient.getMember();
            if (member == null) {
                return 0;
            }
            List<?> wishlists = bookClient.getWishlists(member.memberId());
            return wishlists != null ? wishlists.size() : 0;
        } catch (Exception e) {
            log.warn("Wishlist Badge Error: {}", e.getMessage());
            return 0;
        }
    }

    // accessToken 쿠키 존재 여부 확인
    private boolean hasAccessToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return false;

        HttpServletRequest request = attributes.getRequest();
        Cookie[] cookies = request.getCookies();

        if (cookies == null) return false;

        return Arrays.stream(cookies)
                .anyMatch(cookie -> "accessToken".equals(cookie.getName()));
    }
}