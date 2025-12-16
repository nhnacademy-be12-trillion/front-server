package com.nhnacademy.frontserver;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookClient bookClient;

    @GetMapping({"/", "/index"})
    public String index(Model model) {

        // ✅ 여기 숫자가 작거나(1) 어디선가 size=1이 들어가면 "책 1권"만 나옴
        int page = 0;
        int size = 20;                 // ✅ 넉넉하게
        String sort = "bookId,desc";

        PageResponse<BookListResponse> books = bookClient.getBooks(page, size, sort);

        List<BookListResponse> popularBooks = bookClient.getPopularBooks();
        List<BookListResponse> categoryBooks = bookClient.getBooksByCategory(1L);

        // ✅ index.html이 books.content를 쓰므로 PageResponse 자체를 넣어야 함
        model.addAttribute("books", books);
        model.addAttribute("popularBooks", popularBooks);
        model.addAttribute("categoryBooks", categoryBooks);

        return "index";
    }
}
