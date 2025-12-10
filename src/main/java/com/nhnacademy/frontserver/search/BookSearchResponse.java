package com.nhnacademy.frontserver.search;

import java.util.List;

record BookSearchResponse(
        List<BookSearchResult> results,
        long total,
        int page,
        int size
) {}
