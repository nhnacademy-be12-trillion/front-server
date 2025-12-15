package com.nhnacademy.frontserver.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        int totalPages = (int) Math.ceil(totalCount / (double) res.size());

        // 템플릿에서 쓰는 이름으로 넣기
        model.addAttribute("query", q);
        model.addAttribute("sort", sort.name());
        model.addAttribute("books", res.results());
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPages", totalPages);

        // page.*(first/last/number) “유지”하려면 이걸 넣어줘야 함
        model.addAttribute("page", new PageView(res.page(),
                res.page() == 0,
                res.page() >= totalPages - 1));

        return "search-result";
    }

    record PageView(int number, boolean first, boolean last) {}
}
