package com.nhnacademy.frontserver.layout.cartbadge.service;
import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartSummaryResponseDto;

import com.nhnacademy.frontserver.layout.cartbadge.repository.CartBadgeLocalRepository;
import com.nhnacademy.frontserver.layout.cartbadge.repository.CartBadgeRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartBadgeService {

    private final CartClient cartClient;
    private final CartBadgeLocalRepository l1Cache; // L1 (Caffeine)
    private final CartBadgeRedisRepository l2Cache;      // L2 (Redis)

    /**
     * 조회 (L1 -> L2 -> Feign 자동 주입 호출)
     */
    public int getCartCount(Long memberId, String guestId) {
        // 1. 식별자가 아예 없으면 0개!
        if (memberId == null && (guestId == null || guestId.isEmpty())) return 0;

        // 2. 캐시 키 생성 (이걸 위해 ID를 인자로 받음)
        String cacheKey = generateKey(memberId, guestId); //memberId가 있으면 무조건 멤버로 판단

        // 3. [L1] Caffeine 조회
        Integer l1 = l1Cache.get(cacheKey);
        if (l1 != null) return l1; //L1 캐시 HIT? 반환

        // 4. [L2] Redis 조회
        Integer l2 = l2Cache.get(cacheKey);
        if (l2 != null) { //L1 캐시 MISS 그리고, L2 캐시 HIT?
            l2Cache.put(cacheKey, l2); //L2 캐시에서 받아온 값을 L1 캐시에 warming
            return l2; // 반환
        }

        // 5. L1 캐시 MISS, L2 캐시도 MISS?  => 백엔드 API 호출
        return fetchAndRefresh(cacheKey); // 백엔드 API 호출해서 L1,L2 캐시 모두 warming
    }

    /**
     * 갱신 (Write-Through)
     */
    public void refreshCartCount(Long memberId, String guestId, int newCount) {
        String cacheKey = generateKey(memberId, guestId);
        if (cacheKey != null) {
            updateAllCaches(cacheKey, newCount);
        }
    }

    // --- Private Methods ---

    private int fetchAndRefresh(String cacheKey) {
        try {
            // [핵심 변경] 인자 없이 호출!
            // Interceptor가 현재 요청의 헤더(X-User-Id)와 쿠키(SESSION)를 알아서 심어줌
            CartSummaryResponseDto summary = cartClient.getCartSummary().getBody();

            int count = (summary != null) ? (int) summary.getTotalQuantity() : 0;

            updateAllCaches(cacheKey, count);
            return count;

        } catch (Exception e) {
            log.error("Cart API Summary Fail: key={}", cacheKey, e);
            return 0;
        }
    }

    private void updateAllCaches(String key, int count) {
        l1Cache.put(key, count);
        l2Cache.put(key, count);
    }

    private String generateKey(Long memberId, String guestId) {
        return (memberId != null) ? "MEMBER:" + memberId : "GUEST:" + guestId;
    }
}