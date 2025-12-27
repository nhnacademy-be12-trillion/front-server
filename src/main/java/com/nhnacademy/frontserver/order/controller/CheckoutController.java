package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookDetailResponse;
import com.nhnacademy.frontserver.cart.client.CartClient;
import com.nhnacademy.frontserver.cart.dto.CartResponseDto;
import com.nhnacademy.frontserver.member.AddressResponse;
import com.nhnacademy.frontserver.member.MemberClient;
import com.nhnacademy.frontserver.member.MemberResponse;
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
    private final com.nhnacademy.frontserver.coupon.port.out.MemberCouponClient memberCouponClient;

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

        // 즉시 결제 흐름: bookId + quantity 가 넘어온 경우
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
        // 장바구니 결제 흐름: bookId + quantity 가 없는 경우
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

        // 주문 요약 정보 계산
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

        // 회원 정보 확인 (GlobalControllerAdvice에서 주입된 member 활용)
        MemberResponse member = (MemberResponse) model.getAttribute("member");
        boolean isMember = (member != null);
        
        OrderCreateRequest orderCreateRequest;
        List<AddressResponse> addresses = Collections.emptyList();
        AddressResponse defaultAddress = null;
        List<com.nhnacademy.frontserver.coupon.port.out.MemberCouponResponse> memberCoupons = Collections.emptyList();

        if (isMember) {
            try {
                addresses = memberClient.getAllAddresses();
                if (!addresses.isEmpty()) {
                    defaultAddress = addresses.get(0); // 첫 번째 주소를 기본값으로 사용
                }

                String combinedAddress = null;
                String postCode = null;
                if (defaultAddress != null) {
                    combinedAddress = defaultAddress.addressBase() + " " + defaultAddress.addressDetail();
                    postCode = defaultAddress.addressPostCode();
                }
                
                // 쿠폰 조회 및 필터링 (사용 안 한 쿠폰만)
                try {
                     memberCoupons = memberCouponClient.getMemberCoupons(org.springframework.data.domain.PageRequest.of(0, 100))
                            .stream()
                            .filter(c -> !c.use())
                            .toList();
                } catch (Exception e) {
                    log.warn("쿠폰 목록 조회 실패: {}", e.getMessage());
                }

                orderCreateRequest = new OrderCreateRequest(
                        member.memberName(), member.memberContact(), member.memberEmail(), // 주문자 + 이메일
                        null, // deliveryDate
                        member.memberName(), member.memberContact(), // 수령인 (기본값)
                        combinedAddress,
                        postCode,
                        null, // nonMemberPassword
                        0, // pointUsage
                        null, // couponId
                        null  // orderItems
                );
            } catch (Exception e) {
                log.error("회원 추가 정보(주소 등) 조회 실패", e);
                // 실패 시 기본 빈 객체
                orderCreateRequest = new OrderCreateRequest(null, null, null, null, null, null, null, null, null, 0, null, null);
            }
        } else {
            // 비회원
            orderCreateRequest = new OrderCreateRequest(null, null, null, null, null, null, null, null, null, 0, null, null);
        }

        // 포장 정보 조회
        List<PackagingResponse> packagings = Collections.emptyList();
        try {
            List<PackagingResponse> packagingResponse = orderClient.getAllPackaging(0, 100, "id,asc");
            if (!packagingResponse.isEmpty()) {
                packagings = packagingResponse;
            }
        } catch (Exception e) {
            log.error("포장 정보를 가져오는 데 실패했습니다.", e);
        }

        // 모델과 세션에 데이터 추가
        model.addAttribute("items", items);
        model.addAttribute("orderSummary", orderSummary);
        model.addAttribute("orderCreateRequest", orderCreateRequest);
        model.addAttribute("isMember", isMember); // Model에 이미 있지만 명시적으로 유지
        model.addAttribute("addresses", addresses);
        if (defaultAddress != null) {
            model.addAttribute("defaultAddress", defaultAddress);
        }
        model.addAttribute("memberCoupons", memberCoupons);
        model.addAttribute("packagings", packagings);
        model.addAttribute("deliveryPolicy", policy);
        session.setAttribute("checkoutItems", items);

        return "checkout";
    }
}
