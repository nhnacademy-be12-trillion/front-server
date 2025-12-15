package com.nhnacademy.frontserver.layout.cartbadge.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class CartBadgeRedisRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String KEY_PREFIX = "cart:badge:";
    private static final Duration TTL = Duration.ofHours(24); // 하루 공유

    public Integer get(String cacheKey) {
        String val = redisTemplate.opsForValue().get(KEY_PREFIX + cacheKey);
        return val != null ? Integer.parseInt(val) : null;
    }

    public void put(String cacheKey, int count) {
        redisTemplate.opsForValue().set(KEY_PREFIX + cacheKey, String.valueOf(count), TTL);
    }
}