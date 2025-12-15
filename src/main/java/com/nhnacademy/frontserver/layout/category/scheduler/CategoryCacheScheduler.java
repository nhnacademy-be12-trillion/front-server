package com.nhnacademy.frontserver.layout.category.scheduler;

import com.nhnacademy.frontserver.layout.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryCacheScheduler {

    private final CategoryService categoryService;

    @PostConstruct
    public void init() {
        log.info("Category Cache Scheduler Initialized.");
        categoryService.refreshProcess();
    }

    // 15분 주기 (이전 작업 완료 후 15분 대기)
    @Scheduled(fixedDelay = 900000, initialDelay = 900000)
    public void scheduledRefresh() {
        log.info("Executing Scheduled Category Refresh...");
        categoryService.refreshProcess();
    }
}