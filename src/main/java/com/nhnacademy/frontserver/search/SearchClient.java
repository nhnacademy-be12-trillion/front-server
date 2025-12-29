package com.nhnacademy.frontserver.search;



import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gateway-search",
        url = "${gateway.url}")
public interface SearchClient {

    @GetMapping("/api/search")
    BookSearchResponse search(
            @RequestParam("query") String query,
            @RequestParam(value = "sort", defaultValue = "RELEVANCE") BookSortOption sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @GetMapping("/api/search/ai")
    BookSearchResponse searchAi(
            @RequestParam("query") String query,
            @RequestParam(value = "sort", defaultValue = "RELEVANCE") BookSortOption sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    );

    @PostMapping("/api/search/index/isbn/{isbn}")
    void upsertByIsbn(@PathVariable("isbn") String isbn);

    @PostMapping("/api/search/index/{bookId}")
    void upsertByBookId(@PathVariable("bookId") Long bookId);

    @DeleteMapping("/api/search/index/{bookId}")
    void deleteByBookId(@PathVariable("bookId") Long bookId);
}
