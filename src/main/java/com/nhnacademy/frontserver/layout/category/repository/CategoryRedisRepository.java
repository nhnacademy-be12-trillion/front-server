package com.nhnacademy.frontserver.layout.category.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CategoryRedisRepository {

    // Object 대신 StringRedisTemplate 사용 (직렬화 안전성 확보)
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY = "category:tree";
    private static final long TTL_MINUTES = 60; // 1시간

    public List<CategoryTreeResponse> findAll() {
        try {
            // 1. JSON 문자열로 가져옴
            String json = redisTemplate.opsForValue().get(REDIS_KEY);
            if (json != null) {
                // 2. 정확한 타입으로 역직렬화 (LinkedHashMap 문제 해결)
                return objectMapper.readValue(json, new TypeReference<List<CategoryTreeResponse>>() {});
            }
        } catch (Exception e) {
            log.warn("L2 Cache (Redis) read failed or data missing.", e);
        }
        return null;
    }

    public void saveAll(List<CategoryTreeResponse> data) {
        try {
            // 1. JSON 문자열로 변환
            String json = objectMapper.writeValueAsString(data);
            // 2. 저장
            redisTemplate.opsForValue().set(REDIS_KEY, json, TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("L2 Cache (Redis) saved.");
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize category data", e);
        } catch (Exception e) {
            log.error("Failed to save to L2 Cache (Redis)", e);
        }
    }
}