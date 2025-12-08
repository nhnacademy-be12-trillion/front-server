package com.nhnacademy.frontserver.client;

import com.nhnacademy.frontserver.dto.search.BookSearchResponse;
import com.nhnacademy.frontserver.dto.search.BookSortOption;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "searchClient", url = "https://localhost:10416")
public interface SearchClient {

    @GetMapping("/api/books/search")
    BookSearchResponse search(
            @RequestParam("query") String query,
            @RequestParam(value = "sort", defaultValue = "RELEVANCE") BookSortOption sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );
}
