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

    @GetMapping
    public String viewCartList(Model model) {
        List<CartResponseDto> cartItems = cartClient.getCartItems().getBody();

        // 총 개수 조회 (배지 표기용 - AJAX 안 쓰므로 여기서 같이 호출)
        CartSummaryResponseDto cartSummary = cartClient.getCartSummary().getBody();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("cartSummary", cartSummary);

        return "cart/list";
    }

    /**
     * [동작] 장바구니 담기
     */
    @PostMapping("/add")
    public String addToCart( @RequestBody CartCreateRequestDto requestDto) {
        cartClient.addToCart(requestDto);

        return "redirect:/carts";
    }

    /**
     * [동작] 수량 변경
     */
    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam Long bookId,
            @RequestParam Integer quantity
    ) {
        cartClient.updateCartItem(bookId, new CartUpdateRequestDto(quantity));

        return "redirect:/carts";
    }

    /**
     * [동작] 삭제 (단건)
     */
    @PostMapping("/delete/{bookId}")
    public String deleteItem(@PathVariable Long bookId) {
        cartClient.removeCartItem(bookId);

        return "redirect:/carts";
    }

    /**
     * [동작] 장바구니 비우기 (전체 삭제)
     * - 타임리프 Form에서 action="/carts/clear" method="post"로 호출
     */
    @PostMapping("/clear")
    public String clearCart() {
        cartClient.clearCart();

        return "redirect:/carts";
    }
}