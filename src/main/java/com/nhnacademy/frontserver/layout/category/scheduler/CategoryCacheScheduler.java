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

    // 클러스터 모드 1분 주기/ 비클러스터 모드 10분 주기
    // TODO. 문제 가능 상황: 클러스터모드에서 최대 1분(스케쥴러 주기 만큼) 동안 싱크가 안맞을 수 있음
    @Scheduled(
            fixedDelayString = "#{${app.cluster.enabled:false} ? 60000 : 600000}",
            initialDelayString = "#{${app.cluster.enabled:false} ? 60000 : 600000}"
    )
    public void scheduledRefresh() {
        log.info("Executing Scheduled Category Refresh...");
        categoryService.refreshProcess();
    }
}