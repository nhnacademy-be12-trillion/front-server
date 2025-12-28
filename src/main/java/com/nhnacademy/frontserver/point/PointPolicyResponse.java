package com.nhnacademy.frontserver.point;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointPolicyResponse(
        Long pointPolicyId,
        PointPolicyCode pointPolicyCode,
        String pointPolicyName,
        PointPolicyType pointPolicyType,
        BigDecimal pointPolicyRate,
        Integer pointPolicyFixedAmount,
        LocalDateTime lastModifiedAt
) {}