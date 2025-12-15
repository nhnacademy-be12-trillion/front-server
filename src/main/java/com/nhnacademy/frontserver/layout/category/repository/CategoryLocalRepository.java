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

    private volatile List<CategoryTreeResponse> cachedCategories = Collections.emptyList();
    private final CountDownLatch initLatch = new CountDownLatch(1);

    public void saveAll(List<CategoryTreeResponse> data) {
        // 불변 리스트로 감싸서 저장 (데이터 오염 방지)
        this.cachedCategories = Collections.unmodifiableList(data);

        if (initLatch.getCount() > 0) {
            initLatch.countDown();
            log.info("L1 Cache (Local) Initialized. Latch Opened.");
        }
    }

    public List<CategoryTreeResponse> findAll() {
        try {
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