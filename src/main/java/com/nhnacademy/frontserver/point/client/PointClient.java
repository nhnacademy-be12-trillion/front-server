package com.nhnacademy.frontserver.point.client;

import com.nhnacademy.frontserver.point.PointHistoryResponse;
import com.nhnacademy.frontserver.point.PointPolicyResponse;
import com.nhnacademy.frontserver.point.PointPolicyUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "gateway-member",
        url = "${gateway.url}",
        contextId = "pointClient")
public interface PointClient {

    // 포인트 내역
    @GetMapping("/api/members/points/histories")
    List<PointHistoryResponse> getPointHistories(@RequestHeader("X-Member-Id") Long memberId);

    // 포인트 정책
    @GetMapping("/api/members/admin/points/policies")
    List<PointPolicyResponse> getPolicies();

    @PutMapping("/api/members/admin/points/policies/{policyId}")
    void updatePointPolicy(@PathVariable Long policyId,
                           @RequestBody PointPolicyUpdateRequest request);
}