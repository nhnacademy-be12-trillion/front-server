package com.nhnacademy.frontserver.dto.search;

public record BookSearchResult(
        String id,
        String isbn,
        String title,
        String author,
        String publisher,
        Integer price,
        String imageUrl,
        String editionPublishDate,
        Float score
) {}