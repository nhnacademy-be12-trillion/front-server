package com.nhnacademy.frontserver.layout.category.repository;

import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class CategoryLocalRepository {

    // 읽기 성능 최적화 (락 없이 즉시 반환을 위해 volatile 사용)
    private volatile List<CategoryTreeResponse> cachedCategories = Collections.emptyList();

    // 초기화 대기용 빗장 (서버 시작 직후 데이터 보장)
    private final CountDownLatch initLatch = new CountDownLatch(1);

    public void saveAll(List<CategoryTreeResponse> data) {
        this.cachedCategories = data;
        // 데이터가 적재되면 빗장 해제
        if (initLatch.getCount() > 0) {
            initLatch.countDown();
            log.info("L1 Cache (Local) Initialized. Latch Opened.");
        }
    }

    public List<CategoryTreeResponse> findAll() {
        try {
            // 서버 켜진 직후 데이터가 없을 때만 잠시 대기
            if (initLatch.getCount() > 0) {
                boolean ready = initLatch.await(3, TimeUnit.SECONDS);
                if (!ready) {
                    log.warn("L1 Cache init timeout! Returning empty list.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for L1 cache");
        }
        return this.cachedCategories;
    }

    public boolean isEmpty() {
        return this.cachedCategories.isEmpty();
    }
}