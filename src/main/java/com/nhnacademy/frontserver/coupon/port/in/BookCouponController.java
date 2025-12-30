package com.nhnacademy.frontserver.coupon.port.in;

import com.nhnacademy.frontserver.coupon.port.out.BookCouponClient;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/book-coupons")
@RequiredArgsConstructor
@Slf4j
public class BookCouponController {
    private final BookCouponClient bookCouponClient;
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody BookCouponCreateRequest bookCouponCreateRequest){
        bookCouponClient.saveCoupon(bookCouponCreateRequest);
        return ResponseEntity.ok().build();
    }
    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<String> handleFeignException(FeignException.Unauthorized e, HttpServletRequest req) {
        log.debug("FeignException.Unauthorized", e);
        log.debug("requestUri:{}", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
    }
}
