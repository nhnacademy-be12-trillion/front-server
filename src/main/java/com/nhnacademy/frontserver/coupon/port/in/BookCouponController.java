package com.nhnacademy.frontserver.coupon.port.in;

import com.nhnacademy.frontserver.coupon.port.out.BookCouponClient;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/book-coupons")
@RequiredArgsConstructor
@Slf4j
public class BookCouponController {

    private final BookCouponClient bookCouponClient;

    /**
     * 쿠폰 발급 요청
     * 쿠폰 서버에 요청을 보내고, 정책(도서/카테고리 매칭) 확인 결과를 응답으로 변환합니다.
     */
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody BookCouponCreateRequest bookCouponCreateRequest) {
        try {
            // [1] 쿠폰 서버에 발급 요청 전송
            // (쿠폰 서버 내부에서 도서ID와 카테고리를 확인하여 정책 매칭 여부를 판단함)
            long saveCount = bookCouponClient.saveCoupon(bookCouponCreateRequest);

            // [2] 예외가 없으면 성공 (200 OK)
            return ResponseEntity.ok().build();

        }catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        catch (FeignException.NotFound e) {
            // [3] 404 Not Found: 해당 도서나 카테고리에 적용할 수 있는 쿠폰 정책이 없음
            log.info("발급 가능한 쿠폰 정책 없음 - BookId: {}", bookCouponCreateRequest);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (FeignException.Conflict e) {
            // [4] 409 Conflict: 이미 발급받은 쿠폰임 (중복 발급 방지)
            log.info("쿠폰 중복 발급 시도 - BookId: {}", bookCouponCreateRequest);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (FeignException e) {
            // [5] 기타 서버 오류 (500 등)
            log.error("쿠폰 서버 통신 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ExceptionHandler(FeignException.Unauthorized.class)
    public ResponseEntity<String> handleFeignException(FeignException.Unauthorized e, HttpServletRequest req) {
        log.debug("인증되지 않은 사용자 요청: {}", req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요한 서비스입니다.");
    }
}