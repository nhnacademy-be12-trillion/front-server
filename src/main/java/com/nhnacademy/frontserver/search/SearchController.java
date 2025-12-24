package com.nhnacademy.frontserver.search;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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
            @RequestParam(name = "mode", defaultValue = "bm25") String mode, // bm25 / ai
            Model model
    ) {
        String q = (query == null) ? "" : query.trim();
        boolean aiMode = "ai".equalsIgnoreCase(mode);

        BookSearchResponse res;
        long elapsedMs = 0L;

        if (q.isBlank()) {
            // 검색어 없으면 그냥 빈 결과
            res = new BookSearchResponse(List.of(), 0L, page, size);
        } else {
            long start = System.currentTimeMillis();

            if (aiMode) {
                // AI 검색 엔드포인트 호출
                res = searchClient.searchAi(q, sort, page, size);
            } else {
                // 기존 BM25 검색
                res = searchClient.search(q, sort, page, size);
            }

            elapsedMs = System.currentTimeMillis() - start;
        }

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

        // 헤더/검색폼/뷰에서 쓸 모드 + 시간 정보
        model.addAttribute("mode", aiMode ? "ai" : "bm25");
        model.addAttribute("aiMode", aiMode);
        model.addAttribute("elapsedMs", elapsedMs);

        return "search-result";
    }

    record PageView(int number, boolean first, boolean last) {}
}
