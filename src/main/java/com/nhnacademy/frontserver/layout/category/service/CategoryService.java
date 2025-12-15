package com.nhnacademy.frontserver.layout.category.service;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import com.nhnacademy.frontserver.layout.category.repository.CategoryLocalRepository;
import com.nhnacademy.frontserver.layout.category.repository.CategoryRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BookClient bookClient; // Source
    private final CategoryLocalRepository l1Cache;
    private final CategoryRedisRepository l2Cache;

    /**
     * 외부(Controller)에서 호출하는 메서드.
     * 무조건 L1(Local)에서 빠르게 반환.
     */
    public List<CategoryTreeResponse> getCategories() {
        return l1Cache.findAll();
    }

    /**
     * 캐시 갱신 프로세스 (Scheduler에 의해 호출됨)
     * Flow: Source -> L2(Redis) -> L1(Local)
     */
    public synchronized void refreshProcess() {
        try {
            // 1. Source(Book Server) 조회 시도
            List<CategoryTreeResponse> freshData = fetchFromSourceOrL2();

            if (freshData != null && !freshData.isEmpty()) {
                // 2. L1(Local) 갱신
                l1Cache.saveAll(freshData);
                log.info("Category Cache Refresh Success. Size: {}", freshData.size());
            } else {
                handleFailure("Fetched data is empty");
            }
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    /**
     * 데이터 가져오기 전략:
     * 1순위: Book Server (최신 데이터 보장)
     * 2순위: Book Server 실패시 Redis (기존 데이터 방어)
     */
    private List<CategoryTreeResponse> fetchFromSourceOrL2() {
        try {
            // 원천 데이터 조회
            List<CategoryTreeResponse> data = bookClient.getCategoryTree();
            if (data != null && !data.isEmpty()) {
                // 원천 데이터가 정상이면 L2(Redis)도 최신화
                l2Cache.saveAll(data);
                return data;
            }
        } catch (Exception e) {
            log.error("Book Server connection failed. Trying L2 Cache...", e);
        }

        // 원천 조회 실패 시 L2(Redis)에서 백업 데이터 조회
        return l2Cache.findAll();
    }

    private void handleFailure(String reason) {
        // 기존 L1 데이터가 있으면 그냥 버팀 (서비스 중단 방지)
        if (!l1Cache.isEmpty()) {
            log.warn("Refresh failed, serving old L1 cache. Reason: {}", reason);
            return;
        }

        // L1 데이터가 아예 없으면 비상 상황 -> Fast Retry
        log.error("CRITICAL: All Caches Empty! Triggering Fast-Retry. Reason: {}", reason);
        triggerFastRetry();
    }

    private void triggerFastRetry() {
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                log.info("Retrying refresh process...");
                refreshProcess();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
