package com.nhnacademy.frontserver.member;

import java.math.BigDecimal;

public record GradeResponse(
        Long gradeId,
        String gradeName,
        BigDecimal gradePointRatio,
        Integer gradeCondition
) {}