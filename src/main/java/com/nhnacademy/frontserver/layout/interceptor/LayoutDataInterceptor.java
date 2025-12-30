package com.nhnacademy.frontserver.layout.interceptor;

import com.nhnacademy.frontserver.layout.cartbadge.service.CartBadgeService;
import com.nhnacademy.frontserver.layout.category.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;

@Slf4j
@Component
public class LayoutDataInterceptor implements HandlerInterceptor {

    private final CartBadgeService cartBadgeService;
    private final CategoryService categoryService;

    public LayoutDataInterceptor(@Lazy CartBadgeService cartBadgeService,
                                 @Lazy CategoryService categoryService) {
        this.cartBadgeService = cartBadgeService;
        this.categoryService = categoryService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) {
        // 1. 뷰 렌더링이 불필요한 경우(리다이렉트) 스킵
        if (shouldSkip(mv)) return;

        // 2. 각 영역별 데이터 주입
        addCategoryMenu(mv);
        addCartBadge(mv);
    }

    // --- 1. 카테고리 메뉴용 모델 주입  ---
    private void addCategoryMenu(ModelAndView mv) {
        mv.addObject("categories", categoryService.getCategories());
    }
    // --- 2. 장바구니 뱃지용 모델 주입 ---
    private void addCartBadge(ModelAndView mv){
        int count = cartBadgeService.getCartCount();
        mv.addObject("badgeCount", count);
    }

    // --- Helper Utils ---

    private boolean shouldSkip(ModelAndView mv) {
        return mv == null || (mv.getViewName() != null && mv.getViewName().startsWith("redirect:"));
    }
}