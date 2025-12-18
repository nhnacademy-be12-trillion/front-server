package com.nhnacademy.frontserver.cart.controller;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartWebController {

    private final CartClient cartClient;
    private final BookClient bookClient;

    /**
     * [뷰 전용 DTO] HTML 렌더링을 위해 장바구니+책정보+합계금액을 합친 객체
     */
    @Getter
    @AllArgsConstructor
    public static class CartItemDetailDto {
        private Long id; // bookId (HTML에서 item.id로 사용)
        private BookDetailResponse book; // 책 상세 정보
        private int quantity; // 수량
        private long subtotal; // 소계 (가격 * 수량)
    }

    /**
     * [뷰 전용 DTO] 장바구니 요약 정보
     */
    @Getter
    @AllArgsConstructor
    public static class CartTotalSummaryDto {
        private long subTotal;     // 상품 총 금액
        private long shippingFee;  // 배송비
        private long totalPrice;   // 최종 결제 금액
    }

    /**
     * [장바구니 페이지 조회]
     */
    @GetMapping
    public String viewCartList(Model model) {

        // 1. 장바구니 목록 조회 (bookId만 있음)
        List<CartResponseDto> cartItems = cartClient.getCartItems().getBody();

        List<CartItemDetailDto> viewItems = new ArrayList<>();
        long totalItemPrice = 0;

        // 2. 각 항목별 책 상세 정보 조회 및 가격 계산
        if (cartItems != null) {
            for (CartResponseDto item : cartItems) {
                // FeignClient로 책 정보 조회
                BookDetailResponse bookInfo = bookClient.getBookDetail(item.getBookId());

                // 소계 계산 (판매가 * 수량)
                long subTotal = (long) bookInfo.bookSalePrice() * item.getCartQuantity();
                totalItemPrice += subTotal;

                // 뷰용 객체 생성
                viewItems.add(new CartItemDetailDto(
                        item.getBookId(),
                        bookInfo,
                        item.getCartQuantity(),
                        subTotal
                ));
            }
        }

        // 3. 배송비 정책 (예: 3만원 이상 무료, 아니면 5000원) -> 비즈니스 로직에 맞게 수정 필요
        //long shippingFee = (totalItemPrice > 0 && totalItemPrice < 30000) ? 5000 : 0;
        long shippingFee = 5000;
        long finalPrice = totalItemPrice + shippingFee;

        // 4. 모델에 담기
        model.addAttribute("cartItems", viewItems); // 리스트
        model.addAttribute("cart", new CartTotalSummaryDto(totalItemPrice, shippingFee, finalPrice)); // 요약 정보

        return "cart";
    }

    /**
     * [동작] 장바구니 담기
     */
    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(@RequestBody CartCreateRequestDto requestDto) {
        cartClient.addToCart(requestDto);

        return ResponseEntity.noContent().build();
    }

    /**
     * [동작] 수량 변경
     */
    @PostMapping("/update")
    public String updateQuantity(@RequestParam("bookId") Long bookId,
                                 @RequestParam("quantity") Integer quantity) {
        // 수량은 1보다 작을 수 없음
        if (quantity < 1) quantity = 1;

        cartClient.updateCartItem(bookId, new CartUpdateRequestDto(quantity));
        return "redirect:/carts";
    }

    /**
     * [동작] 삭제
     */
    @PostMapping("/delete/{bookId}")
    public String deleteItem(@PathVariable Long bookId) {
        cartClient.removeCartItem(bookId);
        return "redirect:/carts";
    }

    /**
     * [동작] 비우기
     */
    @PostMapping("/clear")
    public String clearCart() {
        cartClient.clearCart();
        return "redirect:/carts";
    }
}