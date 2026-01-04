package com.nhnacademy.frontserver.point.controller;

import com.nhnacademy.frontserver.member.MemberResponse;
import com.nhnacademy.frontserver.member.client.MemberClient;
import com.nhnacademy.frontserver.point.PointHistoryResponse;
import com.nhnacademy.frontserver.point.client.PointClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;

@Slf4j // 롬복 로그 추가
@Controller
@RequiredArgsConstructor
public class PointHistoryController {

    private final MemberClient memberClient;
    private final PointClient pointClient;

    @GetMapping("/my-page/points")
    public String pointHistoryPage(Model model) {
        MemberResponse member = memberClient.getMember();
        model.addAttribute("currentPoint", member.memberPoint());

        // 로그 확인: 멤버 ID가 제대로 조회되었는지?
        log.info("MyPage Point Lookup - Member ID: {}", member.memberId());

        List<PointHistoryResponse> pointHistories = pointClient.getPointHistories(member.memberId());

        if (pointHistories == null || pointHistories.isEmpty()) {
            log.info("Point History is Empty or Null"); // 비어있다면 로그 출력
            pointHistories = Collections.emptyList();
        } else {
            // 데이터가 있다면 첫 번째 내역 로그 출력
            log.info("Loaded {} histories. First: {}", pointHistories.size(), pointHistories.get(0));
        }

        model.addAttribute("pointHistories", pointHistories);
        return "my/my-points";
    }
}