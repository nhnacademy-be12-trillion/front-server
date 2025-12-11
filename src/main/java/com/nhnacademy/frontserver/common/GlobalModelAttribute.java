package com.nhnacademy.frontserver.common;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.CategoryTreeResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {

    private final BookClient bookClient;

//    @ModelAttribute("categories")
//    public List<CategoryTreeResponse> categories() {
//        try {
//            List<CategoryTreeResponse> result = bookClient.getCategoryTree();
//            log.debug(">>> categories size = {}", (result != null ? result.size() : null));
//            return (result != null) ? result : List.of();   // null 방어
//        } catch (Exception e) {
//            log.debug(">>> categories load failed: {}", e.getMessage(), e);
//            // 여기서 로깅만 하고, 절대 예외를 밖으로 던지지 않는다
//            // log.warn("Fail to load categories", e);
//            return List.of(); // 실패 시 빈 리스트
//        }
//    }
}
