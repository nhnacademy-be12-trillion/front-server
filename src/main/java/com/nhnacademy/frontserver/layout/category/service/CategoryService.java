package com.nhnacademy.frontserver.layout.category.service;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import com.nhnacademy.frontserver.layout.category.repository.CategoryLocalRepository;
import com.nhnacademy.frontserver.layout.category.repository.CategoryRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BookClient bookClient;
    private final CategoryLocalRepository l1Cache;
    private final CategoryRedisRepository l2Cache;

    @Value("${app.cluster.enabled:false}")
    private boolean isClusterMode;

    // 재시도 횟수 제한 (무한 루프 방지)
    private static final int MAX_RETRY_COUNT = 10;
    private final AtomicInteger retryCount = new AtomicInteger(0);

    public List<CategoryTreeResponse> getCategories() {
        return l1Cache.findAll();
    }

    public synchronized void refreshProcess() {
        try {
            List<CategoryTreeResponse> data = null;

            // 1. [Cluster Mode] Redis(L2) 조회
            if (isClusterMode) {
                data = l2Cache.findAll();
            }

            // 2. 데이터가 없으면 Source(Book Server) 조회
            if (data == null || data.isEmpty()) {
                if (isClusterMode) log.info("L2 Cache Miss. Fetching from Source...");

                data = bookClient.getCategoryTree();

                // [Cluster Mode] Redis(L2)에 저장
                if (isClusterMode && data != null && !data.isEmpty()) {
                    l2Cache.saveAll(data);
                }
            }

            // 3. L1 적용 및 성공 처리
            if (data != null && !data.isEmpty()) {
                l1Cache.saveAll(data);
                retryCount.set(0); // 성공하면 재시도 카운트 초기화
                log.debug("Category Refresh Success. Size: {}", data.size());
            } else {
                handleFailure("Fetched data is empty");
            }

        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    private void handleFailure(String reason) {
        if (!l1Cache.isEmpty()) {
            log.warn("Refresh failed, serving old L1 cache. Reason: {}", reason);
            retryCount.set(0);
            return;
        }

        // 재시도 횟수 체크
        if (retryCount.incrementAndGet() > MAX_RETRY_COUNT) {
            log.error("CRITICAL: Max retry attempts ({}) reached. Giving up until next schedule.", MAX_RETRY_COUNT);
            retryCount.set(0);
            return; // 포기
        }

        log.error("CRITICAL: All Caches Empty! Triggering Fast-Retry ({}/{}). Reason: {}", retryCount.get(), MAX_RETRY_COUNT, reason);
        triggerFastRetry();
    }

    private void triggerFastRetry() {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5); // 5초 대기
                refreshProcess();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}