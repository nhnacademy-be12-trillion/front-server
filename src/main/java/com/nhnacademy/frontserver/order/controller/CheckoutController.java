package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartResponseDto;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.order.CheckoutItemView;
import com.nhnacademy.frontserver.order.OrderCreateRequest;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CartClient cartClient;
    private final BookClient bookClient;
    private final MemberClient memberClient;

    // 결제 요약 정보를 담을 DTO
    public record OrderSummary(int subTotal, int shippingFee, int totalPrice) {}

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam(value = "bookId", required = false) Long bookId,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            Model model,
            HttpSession session
    ) {

        List<CheckoutItemView> items = new ArrayList<>();

        // 1) 즉시 결제 흐름: bookId + quantity 가 넘어온 경우
        if (bookId != null && quantity != null) {
            BookDetailResponse book = bookClient.getBookDetail(bookId);
            CheckoutItemView item = new CheckoutItemView(
                    bookId,
                    book.bookName(), // title
                    book.bookImage(), // thumbnailUrl
                    book.bookSalePrice(),
                    quantity,
                    book.bookSalePrice() * quantity
            );
            items.add(item);
        }
        // 2) 장바구니 결제 흐름: bookId + quantity 가 없는 경우
        else {
            try {
                List<CartResponseDto> carts = cartClient.getCartItems().getBody();
                if (carts != null) {
                    for (CartResponseDto cart : carts) {
                        BookDetailResponse book = bookClient.getBookDetail(cart.getBookId());
                        int qty = cart.getCartQuantity();
                        int unitPrice = book.bookSalePrice();
                        CheckoutItemView item = new CheckoutItemView(
                                cart.getBookId(),
                                book.bookName(), // title
                                book.bookImage(), // thumbnailUrl
                                unitPrice,
                                qty,
                                unitPrice * qty
                        );
                        items.add(item);
                    }
                }
            } catch (Exception e) {
            }
        }

        // --- 공통 로직 ---

        // 1. 주문 요약 정보 계산
        int subTotal = items.stream()
                .mapToInt(CheckoutItemView::totalPrice)
                .sum();
        int shippingFee = subTotal >= 50000 ? 0 : 3000; // 5만원 이상 무료배송
        int totalPrice = subTotal + shippingFee;
        OrderSummary orderSummary = new OrderSummary(subTotal, shippingFee, totalPrice);

        // 2. isMember 플래그 확인
        boolean isMember = false;
        try {
            // 회원 정보 조회를 시도하여 로그인 상태 확인
            if (memberClient.getMember() != null) {
                isMember = true;
            }
        } catch (Exception e) {
            // 예외 발생 시 비회원으로 간주
            isMember = false;
        }

        // 3. 배송 정보 폼은 항상 비어있는 OrderCreateRequest 로 준비
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(null, null, null, null, null, null, null, null, 0, null, null);


        // 4. 모델과 세션에 데이터 추가
        model.addAttribute("items", items);
        model.addAttribute("orderSummary", orderSummary);
        model.addAttribute("orderCreateRequest", orderCreateRequest);
        model.addAttribute("isMember", isMember); // isMember 플래그 추가
        session.setAttribute("checkoutItems", items);

        return "checkout";
    }
}
