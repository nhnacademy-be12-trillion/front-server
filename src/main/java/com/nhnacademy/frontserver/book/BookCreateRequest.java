package com.nhnacademy.frontserver.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateRequest {

    // ==========================================
    // 1. Backend API (Record)와 일치시킬 필드들
    // ==========================================
    private String isbn;                // Backend: isbn
    private String bookName;
    private String bookDescription;
    private String bookPublisher;
    private String bookAuthor;
    private String tags;
    private List<Long> categoryIdList;  // Backend: List<Long>

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookPublicationDate; // Backend: bookPublicationDate

    private String bookIndex;
    private Boolean bookPackaging;
    private BookState bookState;        // Enum이 프론트에 없다면 String으로 변경하세요.
    private int bookStock;
    private int bookRegularPrice;
    private int bookSalePrice;
    private String bookImage;


    // HTML name="bookIsbn" -> this.isbn
    public void setBookIsbn(String bookIsbn) {
        this.isbn = bookIsbn;
    }

    // HTML name="bookDate" -> this.bookPublicationDate
    public void setBookDate(LocalDate bookDate) {
        this.bookPublicationDate = bookDate;
    }

    // HTML name="categoryId" (단일 값) -> this.categoryIdList (리스트 변환)
    public void setCategoryId(Long categoryId) {
        if (categoryId != null) {
            this.categoryIdList = Collections.singletonList(categoryId);
        }
    }
}