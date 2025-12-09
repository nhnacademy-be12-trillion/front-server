package com.nhnacademy.frontserver.controller;

import com.nhnacademy.frontserver.controller.order.OrderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my-page")
public class MyPageController {

    private final OrderClient orderClient;

    @GetMapping
    public String myPage(Model model) {

        // TODO: 정상적으로 처리된 주문과 취소된 주문, 반품된 상품들을 추가 필요

        // TODO: mypage.html 에 필요한 다른 데이터 (회원 정보, 리뷰, 주소록 등) 추가 필요
        // model.addAttribute("member", ...);
        // model.addAttribute("reviews", ...);
        // model.addAttribute("addresses", ...);


        return "mypage";
    }
}
