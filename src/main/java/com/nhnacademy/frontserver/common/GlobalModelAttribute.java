package com.nhnacademy.frontserver.common;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import java.util.List;

import com.nhnacademy.frontserver.common.cache.CategoryLocalCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {

    private final CategoryLocalCache categoryLocalCache; // 위에서 만든 로컬 캐시 주입

    @ModelAttribute("categories")
    public List<CategoryTreeResponse> categories() {
        // 네트워크 타지 않음. 0.0001ms 소요.
        return categoryLocalCache.getCategories();
    }
}