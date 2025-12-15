package com.nhnacademy.frontserver.layout.cartbadge.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

// (L1 담당) 로컬 캐시
// 네트워크 IO 지연시간 + 레디스 캐시(L2) 커넥션 풀 절약 용도
@Repository
public class CartBadgeLocalRepository {
    private final Cache<String, Integer> localCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(100_000) // 최대 10만 명 메모리 보호
            .build();

    public Integer get(String key) {
        return localCache.getIfPresent(key);
    }

    public void put(String key, int count) {
        localCache.put(key, count);
    }

    public void evict(String key) { localCache.invalidate(key); }
}