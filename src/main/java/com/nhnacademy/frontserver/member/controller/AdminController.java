package com.nhnacademy.frontserver.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontserver.PageResponse;
import com.nhnacademy.frontserver.book.BookClient;
import com.nhnacademy.frontserver.book.BookCreateRequest;
import com.nhnacademy.frontserver.book.BookState;
import com.nhnacademy.frontserver.book.CategorySearchResponse;
import com.nhnacademy.frontserver.order.OrderItemStatusPatchRequest;
import com.nhnacademy.frontserver.order.OrderResponse;
import com.nhnacademy.frontserver.order.client.OrderClient;
import com.nhnacademy.frontserver.order.util.OrderItemStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final BookClient bookClient;
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;

    // =================================================================================
    // 1. 관리자 페이지 화면 (View)
    // =================================================================================

    @GetMapping("/admin")
    public String adminRoot() {
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {

        List<OrderResponse> orderList = Collections.emptyList();
        try {
            // 정렬 기준: orderDetails.orderDate (내림차순)
            PageResponse<OrderResponse> response = orderClient.getAllOrderByAdmin(0, 200, "orderDetails.orderDate,desc");

            // Record 타입 접근자 .content() 사용
            if (response != null && response.content() != null) {
                orderList = response.content();
            }

            log.info(">>>> [프런트] 관리자 주문 내역 조회 성공: {}건", orderList.size());
        } catch (Exception e) {
            log.error(">>>> [프런트] 주문 내역 조회 실패 (Order Service 통신 오류)", e);
        }

        model.addAttribute("orders", orderList);
        model.addAttribute("activeMenu", "orders");

        // 상태 변경을 위한 Enum 값 전달
        model.addAttribute("itemStatuses", OrderItemStatus.values());

        // 상태값 한글 매핑 맵 생성 (Enum의 getTitle() 활용)
        Map<String, String> statusMap = new HashMap<>();
        
        // OrderStatus 매핑
        for (com.nhnacademy.frontserver.order.util.OrderStatus status : com.nhnacademy.frontserver.order.util.OrderStatus.values()) {
            statusMap.put(status.name(), status.getTitle());
        }
        
        // OrderItemStatus 매핑
        for (OrderItemStatus status : OrderItemStatus.values()) {
            statusMap.put(status.name(), status.getTitle());
        }

        model.addAttribute("statusMap", statusMap);

        return "admin/orders";
    }

    @GetMapping("/admin/policies")
    public String adminPolicies(Model model) {
        model.addAttribute("activeMenu", "policies");

        try {
            // 배송비 정책 조회
            model.addAttribute("deliveryPolicy", orderClient.getDeliveryPolicy());
        } catch (Exception e) {
            log.error("배송비 정책 조회 실패", e);
            model.addAttribute("deliveryPolicy", null);
        }

        try {
            // 포장 정책 조회 (최대 100건 조회)
            model.addAttribute("packagingList", orderClient.getAllPackaging(0, 100, "packagingId,asc"));
        } catch (Exception e) {
            log.error("포장 정책 조회 실패", e);
            model.addAttribute("packagingList", Collections.emptyList());
        }

        // TODO: 포인트 정책 연동 필요
        model.addAttribute("pointPolicy", Map.of("basePointRate", 1.0));

        return "admin/policies";
    }

    @GetMapping("/admin/members")
    public String adminMembers(Model model) {
        model.addAttribute("activeMenu", "members");
        // 회원 목록 로딩 로직 추가 가능
        model.addAttribute("memberGrades", Collections.emptyList());
        return "admin/members";
    }

    @GetMapping("/admin/books")
    public String adminBookRegister(Model model) {
        model.addAttribute("activeMenu", "books");
        return "admin/book-register";
    }

    // =================================================================================
    // 2. 주문 상태 변경 API (AJAX)
    // =================================================================================
    @PatchMapping("/admin/orders/{orderId}/items/{orderItemId}/status")
    @ResponseBody
    public ResponseEntity<String> updateOrderItemStatus(
            @PathVariable("orderId") Long orderId,
            @PathVariable("orderItemId") Long orderItemId,
            @RequestParam("status") OrderItemStatus status
    ) {
        try {
            log.info(">>>> [프런트] 주문 상태 변경 요청: orderId={}, itemId={}, status={}", orderId, orderItemId, status);

            OrderItemStatusPatchRequest request = new OrderItemStatusPatchRequest(status);
            orderClient.patchOrderItemStatusByMember(orderId, orderItemId, request);

            return ResponseEntity.ok("상태 변경 성공");
        } catch (Exception e) {
            log.error(">>>> [프런트] 상태 변경 실패", e);
            return ResponseEntity.status(500).body("상태 변경 실패: " + e.getMessage());
        }
    }

    // =================================================================================
    // 3. 도서 등록 및 검색 API
    // =================================================================================
    @PostMapping("/admin/books")
    public String createBook(
            @ModelAttribute BookCreateRequest request,
            @RequestParam(value = "bookImageFile", required = false) MultipartFile file
    ) {
        try {
            // [BookState 누락 방지]
            if (request.getBookState() == null) {
                request.setBookState(BookState.ON_SALE);
                log.info(">>>> [프런트] BookState 누락 -> ON_SALE로 설정");
            }

            // [추가] 포장 가능 여부(isPackaging) 누락 방지 (기본값 true)
            // 폼에서 값이 넘어오지 않았거나 null일 경우 true로 설정
            if (request.getBookPackaging() == null) {
                request.setBookPackaging(true);
                log.info(">>>> [프런트] isPackaging 누락 -> true로 설정");
            }

            log.info(">>>> 도서 등록 요청: title={}, state={}, packaging={}",
                    request.getBookName(), request.getBookState(), request.getBookPackaging());

            String jsonRequest = objectMapper.writeValueAsString(request);
            MultipartFile jsonPart = new DtoMultipartFile("book", "book.json", "application/json", jsonRequest.getBytes(StandardCharsets.UTF_8));

            bookClient.createBook(jsonPart, file);

            return "redirect:/admin?success=true";
        } catch (Exception e) {
            log.error(">>>> 도서 등록 실패", e);
            return "redirect:/admin?error=create_failed";
        }
    }

    @GetMapping("/admin/books/isbn/{isbn}")
    @ResponseBody
    public ResponseEntity<BookCreateRequest> getBookInfoByIsbn(@PathVariable("isbn") String isbn) {
        try {
            return ResponseEntity.ok(bookClient.getBookInfoByIsbn(isbn));
        } catch (Exception e) {
            return ResponseEntity.status(502).build();
        }
    }

    @GetMapping("/admin/categories/search")
    @ResponseBody
    public ResponseEntity<List<CategorySearchResponse>> searchCategories(@RequestParam("keyword") String keyword) {
        try {
            return ResponseEntity.ok(bookClient.searchCategories(keyword));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO Wrapper Class
    private static class DtoMultipartFile implements MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public DtoMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }
        @Override public String getName() { return name; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return content == null || content.length == 0; }
        @Override public long getSize() { return content.length; }
        @Override public byte[] getBytes() throws IOException { return content; }
        @Override public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(content); }
        @Override public void transferTo(File dest) throws IOException, IllegalStateException { throw new UnsupportedOperationException(); }
    }
}