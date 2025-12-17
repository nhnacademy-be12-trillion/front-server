package com.nhnacademy.frontserver.layout.interceptor;

import com.nhnacademy.frontserver.common.AuthConst;
import com.nhnacademy.frontserver.layout.cartbadge.service.CartBadgeService;
import com.nhnacademy.frontserver.layout.category.service.CategoryService;
import com.nhnacademy.frontserver.layout.logininfo.LoginInfo;
import com.nhnacademy.frontserver.layout.logininfo.service.LoginInfoService;
import jakarta.servlet.http.Cookie;
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
    private final LoginInfoService loginInfoService;

    public LayoutDataInterceptor(@Lazy CartBadgeService cartBadgeService,
                                 @Lazy CategoryService categoryService,
                                 @Lazy LoginInfoService loginInfoService) {
        this.cartBadgeService = cartBadgeService;
        this.categoryService = categoryService;
        this.loginInfoService = loginInfoService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) {
        // 1. 뷰 렌더링이 불필요한 경우(리다이렉트) 스킵
        if (shouldSkip(mv)) return;

        // 2. 각 영역별 데이터 주입
        addCategoryMenu(mv);
        addCartBadge(request, mv);
        addMemberInfo(request, mv);
    }

    // --- 1. 카테고리 메뉴용 모델 주입  ---
    private void addCategoryMenu(ModelAndView mv) {
        mv.addObject("categories", categoryService.getCategories());
    }
    // --- 2. 장바구니 뱃지용 모델 주입 ---
    private void addCartBadge(HttpServletRequest request, ModelAndView mv) {
        Long memberId = parseLongHeader(request, AuthConst.HEADER_MEMBER_ID);
        String guestId = findCookieValue(request, AuthConst.COOKIE_GUEST_ID);

        int count = cartBadgeService.getCartCount(memberId, guestId);
        mv.addObject("badgeCount", count);
    }
    // --- 3. 회원 정보용 모델 주입 ---
    private void addMemberInfo(HttpServletRequest request, ModelAndView mv) {
        LoginInfo member = loginInfoService.getLoginInfo();
        mv.addObject("member", member);
    }

    // --- Helper Utils ---

    private boolean shouldSkip(ModelAndView mv) {
        return mv == null || (mv.getViewName() != null && mv.getViewName().startsWith("redirect:"));
    }

    private Long parseLongHeader(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        try {
            return value != null ? Long.valueOf(value) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String findCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}