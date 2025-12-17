package com.nhnacademy.frontserver.search;

import java.util.List;

public record BookSearchResponse(
        List<BookSearchResult> results,
        long total,
        int page,
        int size
) {}
