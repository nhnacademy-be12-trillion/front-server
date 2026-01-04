package com.nhnacademy.frontserver.layout.interceptor;

import com.nhnacademy.frontserver.layout.badge.CartBadgeService;
import com.nhnacademy.frontserver.layout.badge.WishlistBadgeService;
import com.nhnacademy.frontserver.layout.category.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LayoutDataInterceptor implements HandlerInterceptor {

    private final CartBadgeService cartBadgeService;
    private final CategoryService categoryService;
    private final WishlistBadgeService wishlistBadgeService;

    public LayoutDataInterceptor(@Lazy CartBadgeService cartBadgeService,
                                 @Lazy CategoryService categoryService,
                                 @Lazy WishlistBadgeService wishlistBadgeService
    ) {
        this.cartBadgeService = cartBadgeService;
        this.categoryService = categoryService;
        this.wishlistBadgeService = wishlistBadgeService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 1. 뷰 렌더링이 불필요한 경우(리다이렉트) 스킵
        if (shouldSkip(modelAndView)) return;

        // 2. 각 영역별 데이터 주입
        addCategoryMenu(modelAndView);
        addBadge(modelAndView);
    }

    // --- 1. 카테고리 메뉴용 모델 주입  ---
    private void addCategoryMenu(ModelAndView modelAndView) {
        modelAndView.addObject("categories", categoryService.getCategories());
    }
    // --- 장바구니, 위시리스트 뱃지용 모델 주입 ---
    private void addBadge(ModelAndView modelAndView){
        int count = cartBadgeService.getCartCount();
        modelAndView.addObject("badgeCount", count);

        int wishlistCount = wishlistBadgeService.getWishlistCount();
        modelAndView.addObject("wishlistCount", wishlistCount);
    }

    // --- Helper Utils ---

    private boolean shouldSkip(ModelAndView modelAndView) {
        return modelAndView == null || (modelAndView.getViewName() != null && modelAndView.getViewName().startsWith("redirect:"));
    }
}