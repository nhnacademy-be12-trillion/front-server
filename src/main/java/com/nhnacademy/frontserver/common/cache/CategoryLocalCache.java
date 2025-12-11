package com.nhnacademy.frontserver.common.cache;

import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import com.nhnacademy.frontserver.common.service.CategorySourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryLocalCache {

    private final CategorySourceService categorySourceService;

    // 읽기 성능 최적화 (락 없이 즉시 반환)
    private volatile List<CategoryTreeResponse> cachedCategories = Collections.emptyList();

    // 초기화 대기용 빗장
    // 지금 막 서버가 시작되어, 아직 서버 메모리에 데이터가 적재 되지 않았는데 데이터 요청시 대기 처리용 빗장
    private final CountDownLatch initLatch = new CountDownLatch(1);

    @PostConstruct
    public void init() {
        log.info("Category Cache Init Started...");
        refreshCache();
    }

    // (900000=15분)분마다 실행 (fixedDelay: 이전 작업 끝나고 15분 뒤)
    // 스케줄락 금지
    @Scheduled(fixedDelay = 900000, initialDelay = 900000)
    public void scheduledRefresh() {
        refreshCache();
    }

    /**
     * 캐시 갱신 로직 (동기화 처리됨)
     */
    public synchronized void refreshCache() {
        try {
            List<CategoryTreeResponse> remoteData = categorySourceService.fetchCategoriesFromRemote();

            if (remoteData != null && !remoteData.isEmpty()) {
                this.cachedCategories = remoteData;

                // 성공했으므로 빗장 해제 (최초 1회만 동작)
                if (initLatch.getCount() > 0) {
                    initLatch.countDown();
                    log.info("Category Cache Initialized Successfully. Latch Opened.");
                }
                log.debug("Category Cache Updated. Size: {}", remoteData.size());
            } else {
                handleFailure("Remote data is empty");
            }
        } catch (Exception e) {
            handleFailure(e.getMessage());
        }
    }

    private void handleFailure(String reason) {
        // 1. 아직 데이터가 있으면: 로그만 남기고 기존 데이터 유지 (정상 서비스 가능)
        if (!cachedCategories.isEmpty()) {
            log.warn("Failed to refresh categories, serving old cache. Reason: {}", reason);
            return;
        }

        // 2. 데이터가 아예 없으면: 심각한 상황 -> 재시도 로직 가동 (정상 서비스 불가능)
        log.error("CRITICAL: Cache Init Failed & Empty! Starting Fast-Retry. Reason: {}", reason);
        triggerFastRetry();
    }

    // 백그라운드 스레드에서 5초 마다 재시도
    private void triggerFastRetry() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                log.info("Retrying category cache init...");
                refreshCache();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    // Global하게 Model적재를 위해 ControllerAdvice 등에서 호출해서 사용
    public List<CategoryTreeResponse> getCategories() {
        try {
            // 서버 켜진 직후 3초간은 기다려줌
            if (initLatch.getCount() > 0) {
                boolean ready = initLatch.await(3, TimeUnit.SECONDS);
                if (!ready) {
                    log.warn("Category cache init timeout! Returning empty list.");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for category cache");
        }
        return this.cachedCategories;
    }
}