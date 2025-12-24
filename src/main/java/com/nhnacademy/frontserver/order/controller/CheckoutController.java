package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartResponseDto;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.order.CheckoutItemView;
import com.nhnacademy.frontserver.order.DeliveryPolicyResponse;
import com.nhnacademy.frontserver.order.OrderCreateRequest;
import com.nhnacademy.frontserver.order.PackagingResponse;
import com.nhnacademy.frontserver.order.client.OrderClient;
import java.util.ArrayList;
import java.util.Collections;
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
    private final OrderClient orderClient;

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
        }

        // --- 공통 로직 ---

        // 1. 주문 요약 정보 계산
        int subTotal = items.stream()
                .mapToInt(CheckoutItemView::totalPrice)
                .sum();

        // 배송비 동적 조회
        DeliveryPolicyResponse policy = null;
        int shippingFee;
        try {
            policy = orderClient.getDeliveryPolicy();
            shippingFee = subTotal >= policy.deliveryPolicyThreshold() ? 0 : policy.deliveryPolicyFee();
        } catch (Exception e) {
            log.error("배송비 정책을 가져오는 데 실패했습니다. 기본값으로 설정됩니다.", e);
            shippingFee = subTotal >= 50000 ? 0 : 3000; // Fallback to default
        }

        int totalPrice = subTotal + shippingFee;
        OrderSummary orderSummary = new OrderSummary(subTotal, shippingFee, totalPrice);

        // 2. isMember 플래그 확인
        boolean isMember = false;
        try {
            if (memberClient.getMember() != null) {
                isMember = true;
            }
        } catch (Exception e) {
            isMember = false;
        }

        // 3. 배송 정보 폼은 항상 비어있는 OrderCreateRequest 로 준비
        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(null, null, null, null, null, null, null, null, 0, null, null);

        // 4. 포장 정보 조회
        List<PackagingResponse> packagings = Collections.emptyList();
        try {
            List<PackagingResponse> packagingResponse = orderClient.getAllPackaging(0, 100, "id,asc");
            if (!packagingResponse.isEmpty()) {
                packagings = packagingResponse;
            }
        } catch (Exception e) {
            log.error("포장 정보를 가져오는 데 실패했습니다.", e);
        }

        // 5. 모델과 세션에 데이터 추가
        model.addAttribute("items", items);
        model.addAttribute("orderSummary", orderSummary);
        model.addAttribute("orderCreateRequest", orderCreateRequest);
        model.addAttribute("isMember", isMember);
        model.addAttribute("packagings", packagings);
        model.addAttribute("deliveryPolicy", policy); // 배송 정책 정보를 모델에 추가
        session.setAttribute("checkoutItems", items);

        return "checkout";
    }
}
