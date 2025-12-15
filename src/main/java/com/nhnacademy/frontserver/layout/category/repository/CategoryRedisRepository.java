package com.nhnacademy.frontserver.layout.category.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY = "category:tree";
    private static final long TTL_MINUTES = 60; // 1시간

    public List<CategoryTreeResponse> findAll() {
        try {
            Object cachedData = redisTemplate.opsForValue().get(REDIS_KEY);
            if (cachedData != null) {
                return objectMapper.convertValue(cachedData, new TypeReference<List<CategoryTreeResponse>>() {});
            }
        } catch (Exception e) {
            log.warn("L2 Cache (Redis) connection failed.", e);
        }
        return null;
    }

    public void saveAll(List<CategoryTreeResponse> data) {
        try {
            redisTemplate.opsForValue().set(REDIS_KEY, data, TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("L2 Cache (Redis) saved.");
        } catch (Exception e) {
            log.error("Failed to save to L2 Cache (Redis)", e);
        }
    }
}