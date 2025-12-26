package com.nhnacademy.frontserver.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategorySearchResponse {
    private Long categoryId;
    private String categoryName;
}