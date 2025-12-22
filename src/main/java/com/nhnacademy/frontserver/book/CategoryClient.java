package com.nhnacademy.frontserver.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "gateway-book", url = "${gateway.url}", contextId = "categoryClient")
public interface CategoryClient {

    // 백엔드: CategoryController.getAllCategories() 매핑
    @GetMapping("/api/books/categories")
    List<CategoryTreeResponse> getCategoryTree();
}