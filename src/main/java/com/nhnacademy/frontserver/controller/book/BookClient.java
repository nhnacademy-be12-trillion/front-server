/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2025. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.frontserver.controller.book;

import com.nhnacademy.frontserver.controller.PageResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "book-service", url = "http://localhost:10413")
public interface BookClient {

    //TODO 현재 publisherName 호출 시 null
    // Pageable 응답 예시
    @GetMapping
    PageResponse<BookListResponse> getBooks(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sort
    );

    @GetMapping
    BookDetailResponse getBookDetail(@RequestParam("id") long id);

    //TODO category 뎁스를 노출하는 추천로직 @은해
    /*
    *public CategoryTreeResponse toTreeDto(Category category) {
    return new CategoryTreeResponse(
            category.getCategoryId(),
            category.getCategoryName(),
            category.getChildren().stream()
                    .map(this::toTreeDto)
                    .toList()
    );
}

    * @OneToMany(mappedBy = "parent")
private List<Category> children = new ArrayList<>();
    *
    *
     */

    @GetMapping("/api/categories")
    CategoryTreeResponse getCategoryTree();
}
