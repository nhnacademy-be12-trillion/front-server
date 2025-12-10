package com.nhnacademy.frontserver.order;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "order-service", url = "http://localhost:10407")
public interface CartClient {
    //TODO 승현 컨트롤러 작업 1차 마무리 -> 구현 부탁드립니다
    @GetMapping("/api/carts")
    List<CartResponse> getCarts();
}
