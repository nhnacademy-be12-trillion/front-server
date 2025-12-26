package com.nhnacademy.frontserver.order.controller;

import com.nhnacademy.frontserver.order.DeliveryPolicyUpdateRequest;
import com.nhnacademy.frontserver.order.PackagingCreateRequest;
import com.nhnacademy.frontserver.order.PackagingUpdateRequest;
import com.nhnacademy.frontserver.order.client.OrderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin/policies")
@RequiredArgsConstructor
public class AdminOrderPolicyController {

    private final OrderClient orderClient;

    // 배송비 정책 수정
    @PostMapping("/delivery")
    public String updateDeliveryPolicy(@ModelAttribute DeliveryPolicyUpdateRequest request,
                                       RedirectAttributes redirectAttributes) {
        try {
            orderClient.updateDeliveryPolicy(request);
            redirectAttributes.addFlashAttribute("successMessage", "배송비 정책이 수정되었습니다.");
        } catch (Exception e) {
            log.error("배송비 정책 수정 실패", e);
            redirectAttributes.addFlashAttribute("errorMessage", "배송비 정책 수정 실패: " + e.getMessage());
        }
        return "redirect:/admin/policies";
    }

    // 포장 정책 추가
    @PostMapping("/packaging")
    public String createPackaging(@ModelAttribute PackagingCreateRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            orderClient.createPackaging(request);
            redirectAttributes.addFlashAttribute("successMessage", "포장지가 추가되었습니다.");
        } catch (Exception e) {
            log.error("포장지 추가 실패", e);
            redirectAttributes.addFlashAttribute("errorMessage", "포장지 추가 실패: " + e.getMessage());
        }
        return "redirect:/admin/policies";
    }

    // 포장 정책 수정
    @PostMapping("/packaging/{packagingId}/update")
    public String updatePackaging(@PathVariable Long packagingId,
                                  @ModelAttribute PackagingUpdateRequest request,
                                  RedirectAttributes redirectAttributes) {
        try {
            orderClient.updatePackaging(packagingId, request);
            redirectAttributes.addFlashAttribute("successMessage", "포장지 가격이 수정되었습니다.");
        } catch (Exception e) {
            log.error("포장지 수정 실패", e);
            redirectAttributes.addFlashAttribute("errorMessage", "포장지 수정 실패: " + e.getMessage());
        }
        return "redirect:/admin/policies";
    }

    // 포장 정책 삭제
    @PostMapping("/packaging/{packagingId}/delete")
    public String deletePackaging(@PathVariable Long packagingId,
                                  RedirectAttributes redirectAttributes) {
        try {
            orderClient.removePackaging(packagingId);
            redirectAttributes.addFlashAttribute("successMessage", "포장지가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("포장지 삭제 실패", e);
            redirectAttributes.addFlashAttribute("errorMessage", "포장지 삭제 실패 (사용 중인 포장지일 수 있습니다): " + e.getMessage());
        }
        return "redirect:/admin/policies";
    }
}
