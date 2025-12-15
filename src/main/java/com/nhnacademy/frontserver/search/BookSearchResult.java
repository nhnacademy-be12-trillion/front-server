package com.nhnacademy.frontserver.search;

import java.util.List;

record BookSearchResult(
        String id,
        String isbn,
        String title,
        String author,
        String publisher,
        Integer price,
        String imageUrl,
        String editionPublishDate,
        List<String> tags,
        Float score
) {}