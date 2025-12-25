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

import com.nhnacademy.frontserver.order.DeliveryPolicyResponse;
import com.nhnacademy.frontserver.order.DeliveryPolicyUpdateRequest;
import com.nhnacademy.frontserver.order.PackagingCreateRequest;
import com.nhnacademy.frontserver.order.PackagingResponse;
import com.nhnacademy.frontserver.order.PackagingUpdateRequest;
import com.nhnacademy.frontserver.order.client.OrderClient;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminPageController {

    private final OrderClient orderClient;

    @GetMapping("/api/admin")
    public String adminPage(Model model, @RequestParam(defaultValue = "1") int page) {

        Object mock = new Object();

        // 1. 배송비 정책 조회
        try {
            DeliveryPolicyResponse deliveryPolicy = orderClient.getDeliveryPolicy();
            model.addAttribute("deliveryPolicy", deliveryPolicy);
        } catch (Exception e) {
            log.error("배송비 정책 조회 실패", e);
            model.addAttribute("deliveryPolicy", null);
        }

        // 2. 포장 정책 목록 조회
        try {
            List<PackagingResponse> packagingList = orderClient.getAllPackaging(0, 100, "packagingId,asc");
            model.addAttribute("packagingList", packagingList);
        } catch (Exception e) {
            log.error("포장 정책 조회 실패", e);
            model.addAttribute("packagingList", Collections.emptyList());
        }

        model.addAttribute("orderDetail", mock);
        model.addAttribute("orderList", mock);
        // model.addAttribute("packagePolicy", mock); // Removed redundant
        // model.addAttribute("packageList", mock);   // Removed redundant
        model.addAttribute("pointPolicy", mock);
        model.addAttribute("gradePolicy", mock);
        model.addAttribute("memberList", mock);

        // 3. 회원 관리 (초기에는 null)
        // memberInfo는 검색 버튼을 눌렀을 때만 모델에 추가됩니다.

        return "admin";
    }

    @PostMapping("/admin/policy/delivery")
    public String updateDeliveryPolicy(DeliveryPolicyUpdateRequest request) {
        orderClient.updateDeliveryPolicy(request);
        return "redirect:/api/admin?tab=policy";
    }

    @PostMapping("/admin/policy/packaging")
    public String createPackaging(PackagingCreateRequest request) {
        orderClient.createPackaging(request);
        return "redirect:/api/admin?tab=policy";
    }

    @PostMapping("/admin/policy/packaging/update")
    public String updatePackaging(@RequestParam Long packagingId, @RequestParam int packagingPrice) {
        orderClient.updatePackaging(packagingId, new PackagingUpdateRequest(packagingPrice));
        return "redirect:/api/admin?tab=policy";
    }

    @PostMapping("/admin/policy/packaging/delete/{id}")
    public String deletePackaging(@PathVariable Long id) {
        orderClient.removePackaging(id);
        return "redirect:/api/admin?tab=policy";
    }
}
