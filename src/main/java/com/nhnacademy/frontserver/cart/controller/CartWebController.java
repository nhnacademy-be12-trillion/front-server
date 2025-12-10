package com.nhnacademy.frontserver.cart.controller;

import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
// TODO 1... 타 API Client 이용해서 더 많은 정보 담아야 함.
// ToDO 2... 글로벌 헤더 (장바구니 담은 개수 or 장바구니 담은 종류) 어떻게 할건지...
public class CartWebController {

    private final CartClient cartClient;

    private Long getCurrentMemberId(HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            try {
                return Long.parseLong(userIdHeader);
            } catch (NumberFormatException e) {
                log.error("Gateway passed invalid X-User-Id: {}", userIdHeader);
            }
        }
        return null;
    }

    private String getCookieString(HttpServletRequest request) {
        return request.getHeader("Cookie");
    }

    /**
     * [페이지] 장바구니 목록 조회
     * - AJAX 없이 화면 렌더링 시점에 '총 개수'도 같이 조회해서 모델에 넣습니다.
     * - HTML에서 ${cartCount} 로 사용 가능
     */
    @GetMapping
    public String viewCartList(Model model, HttpServletRequest request) {
        Long memberId = getCurrentMemberId(request);
        String cookieHeader = getCookieString(request);

        // 목록 조회
        List<CartResponseDto> cartItems = cartClient.getCartItems(memberId, cookieHeader).getBody();

        // 총 개수 조회 (배지 표기용 - AJAX 안 쓰므로 여기서 같이 호출)
        Long count = cartClient.countCartItems(memberId, cookieHeader).getBody();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartCount", count); // 타임리프용 변수 추가

        return "cart/list";
    }

    /**
     * [동작] 장바구니 담기
     */
    @PostMapping("/add")
    public String addToCart(@ModelAttribute CartCreateRequestDto dto, HttpServletRequest request) {
        Long memberId = getCurrentMemberId(request);
        String cookieHeader = getCookieString(request);

        cartClient.addToCart(memberId, cookieHeader, dto);

        return "redirect:/carts";
    }

    /**
     * [동작] 수량 변경
     */
    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam Long bookId,
            @RequestParam Integer quantity,
            HttpServletRequest request
    ) {
        Long memberId = getCurrentMemberId(request);
        String cookieHeader = getCookieString(request);

        cartClient.updateCartItem(memberId, cookieHeader, bookId, new CartUpdateRequestDto(quantity));

        return "redirect:/carts";
    }

    /**
     * [동작] 삭제 (단건)
     */
    @PostMapping("/delete/{bookId}")
    public String deleteItem(@PathVariable Long bookId, HttpServletRequest request) {
        Long memberId = getCurrentMemberId(request);
        String cookieHeader = getCookieString(request);

        cartClient.removeCartItem(memberId, cookieHeader, bookId);

        return "redirect:/carts";
    }

    /**
     * [동작] 장바구니 비우기 (전체 삭제)
     * - 타임리프 Form에서 action="/carts/clear" method="post"로 호출
     */
    @PostMapping("/clear")
    public String clearCart(HttpServletRequest request) {
        Long memberId = getCurrentMemberId(request);
        String cookieHeader = getCookieString(request);

        cartClient.clearCart(memberId, cookieHeader);

        return "redirect:/carts";
    }
}