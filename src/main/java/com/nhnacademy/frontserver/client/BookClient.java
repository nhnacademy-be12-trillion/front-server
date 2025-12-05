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

package com.nhnacademy.frontserver.client;

import com.nhnacademy.frontserver.dto.book.BookListResponse;
import com.nhnacademy.frontserver.dto.book.CategoryResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "bookClient", url = "http://localhost:10413/")
public interface BookClient {

    @GetMapping("/api/books/categories}")
    List<CategoryResponse> getCategories(); //뎁스 맞춰서 반환할 수 있게

    @GetMapping("/api/books/list")
    List<BookListResponse> getBooks();
}
