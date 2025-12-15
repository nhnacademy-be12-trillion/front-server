package com.nhnacademy.frontserver.layout.cartbadge.service;

import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartSummaryResponseDto;
import com.nhnacademy.frontserver.infra.RedisListenerConfig;
import com.nhnacademy.frontserver.layout.cartbadge.repository.CartBadgeLocalRepository;
import com.nhnacademy.frontserver.layout.cartbadge.repository.CartBadgeRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartBadgeService {

    private final CartClient cartClient;
    private final CartBadgeLocalRepository l1Cache; // L1 (Caffeine)
    private final CartBadgeRedisRepository l2Cache; // L2 (Redis)
    private final StringRedisTemplate redisTemplate; // 메시지 발행용

    // [NEW] 클러스터 모드 여부 주입 (기본값 false)
    // 로컬 개발 환경에서는 false로 설정하여 Pub/Sub 부하를 제거함
    @Value("${app.cluster.enabled:false}")
    private boolean isClusterMode;

    /**
     * 조회 (L1 -> L2 -> Feign 자동 주입 호출)
     */
    public int getCartCount(Long memberId, String guestId) {
        // 1. 식별자가 아예 없으면 0개
        if (memberId == null && (guestId == null || guestId.isEmpty())) return 0;

        String cacheKey = generateKey(memberId, guestId);

        // 2. [L1] Caffeine 조회 (가장 빠름)
        Integer l1 = l1Cache.get(cacheKey);
        if (l1 != null) return l1;

        // 3. [L2] Redis 조회
        Integer l2 = l2Cache.get(cacheKey);
        if (l2 != null) {
            // [FIX] Redis에 있는 값을 내 로컬(L1)에도 채워둠 (Warming)
            l1Cache.put(cacheKey, l2);
            return l2;
        }

        // 4. [Source] 백엔드 API 호출
        return fetchAndRefresh(cacheKey);
    }

    /**
     * 갱신 (Write-Through)
     */
    public void refreshCartCount(Long memberId, String guestId, int newCount) {
        String cacheKey = generateKey(memberId, guestId);
        if (cacheKey != null) {
            // 1. L1(내꺼), L2(Redis) 값 갱신 (항상 실행)
            updateAllCaches(cacheKey, newCount);

            // 2. 클러스터 모드일 때만 Pub/Sub 메시지 발송!
            // 단일 서버라면 굳이 메시지를 쏠 필요가 없음 (이미 updateAllCaches로 내껀 갱신됨)
            if (isClusterMode) {
                redisTemplate.convertAndSend(RedisListenerConfig.CART_BADGE_TOPIC, cacheKey);
                log.debug("Cluster mode ON: Pub/Sub message sent for key {}", cacheKey);
            }
        }
    }

    // --- Private Methods ---

    private int fetchAndRefresh(String cacheKey) {
        try {
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