package com.nhnacademy.frontserver.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategorySourceService {

    private final BookClient bookClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper; // JSON 변환용

    private static final String REDIS_KEY = "category:tree";
    private static final long TTL_MINUTES = 60; // Redis 보관 기간 (1시간)

    /**
     * Redis에서 가져오거나, 없으면 Book Server에서 가져와서 Redis에 굽는다.
     */
    public List<CategoryTreeResponse> fetchCategoriesFromRemote() {
        try {
            // 1. Redis 조회
            Object cachedData = redisTemplate.opsForValue().get(REDIS_KEY);
            if (cachedData != null) {
                // Redis에 있는 데이터 리턴 (ObjectMapper로 변환)
                return objectMapper.convertValue(cachedData, new TypeReference<List<CategoryTreeResponse>>() {});
            }
        } catch (Exception e) {
            log.warn("Redis connection failed, fallback to direct API call.", e);
        }

        // 2. Redis에 없거나 오류나면 -> Book Server 직접 호출 (Cache Miss)
        log.info("Cache Miss! Fetching categories from Book Server...");
        List<CategoryTreeResponse> freshData = bookClient.getCategoryTree();

        // 3. Redis에 저장 (다음 칭구들을 위해)
        if (freshData != null && !freshData.isEmpty()) {
            try {
                redisTemplate.opsForValue().set(REDIS_KEY, freshData, TTL_MINUTES, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("Failed to save categories to Redis", e);
            }
        }

        return freshData != null ? freshData : List.of();
    }
}