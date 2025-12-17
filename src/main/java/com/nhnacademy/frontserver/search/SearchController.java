package com.nhnacademy.frontserver.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchClient searchClient;

    @GetMapping("/search")
    public String searchPage(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "RELEVANCE") BookSortOption sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        String q = (query == null) ? "" : query.trim();

        BookSearchResponse res = q.isBlank()
                ? new BookSearchResponse(java.util.List.of(), 0L, page, size)   // :contentReference[oaicite:6]{index=6}
                : searchClient.search(q, sort, page, size);                    // :contentReference[oaicite:7]{index=7}

        long totalCount = res.total();

        int pageSize = (res.size() <= 0 ? size : res.size());
        int totalPages = (pageSize <= 0) ? 0 : (int) Math.ceil(totalCount / (double) pageSize);

        int currentPage = Math.max(0, res.page());
        boolean first = currentPage == 0;
        boolean last = (totalPages == 0) || (currentPage >= totalPages - 1);

        model.addAttribute("query", q);
        model.addAttribute("sort", sort.name());
        model.addAttribute("size", pageSize);
        model.addAttribute("result", res.results());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("page", new PageView(currentPage, first, last));


        return "search-result";
    }

    record PageView(int number, boolean first, boolean last) {}
}
