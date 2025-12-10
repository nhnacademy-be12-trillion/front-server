/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2025. NHN Academy Corp. All rights reserved.
 * + * While every precaution has been taken in the preparation of this resource,  assumes no
 * + responsibility for errors or omissions, or for damages resulting from the use of the information
 * + contained herein
 * + No part of this resource may be reproduced, stored in a retrieval system, or transmitted, in any
 * + form or by any means, electronic, mechanical, photocopying, recording, or otherwise, without the
 * + prior written permission.
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.frontserver;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminPageController {


    @GetMapping("/api/admin")
    public String adminPage(Model model, @RequestParam(defaultValue = "1") int page) {

        Object mock = new Object();

        model.addAttribute("orderDetail", mock);
        model.addAttribute("deliveryPolicy", mock);
        model.addAttribute("orderList", mock);
        model.addAttribute("packagePolicy", mock);
        model.addAttribute("packageList", mock);
        model.addAttribute("pointPolicy", mock);
        model.addAttribute("gradePolicy", mock);
        model.addAttribute("memberList", mock);

//        // 1. 주문 목록 (최근 200건 중 1페이지 20건 가정)
//
//        // 2. 정책 설정 (더미 데이터)
//        model.addAttribute("packagingList", List.of(
//                new PackagingPolicy("일반 포장", 0),
//                new PackagingPolicy("고급 선물 포장", 3000)
//        ));
//        model.addAttribute("memberGrades", List.of(
//                new GradePolicy("COMMON", "일반", 1.0),
//                new GradePolicy("ROYAL", "로얄", 1.5),
//                new GradePolicy("GOLD", "골드", 2.0),
//                new GradePolicy("PLATINUM", "플래티넘", 3.0)
//        ));

        // 3. 회원 관리 (초기에는 null)
        // memberInfo는 검색 버튼을 눌렀을 때만 모델에 추가됩니다.

        return "admin";
    }
}
