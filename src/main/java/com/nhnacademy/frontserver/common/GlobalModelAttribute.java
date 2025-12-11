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

    private final CategoryLocalCache categoryLocalCache;

    @ModelAttribute("categories")
    public List<CategoryTreeResponse> categories() {
        // 네트워크 IO 대기 없음
        return categoryLocalCache.getCategories();
    }
}