package com.nhnacademy.frontserver.member;

import java.math.BigDecimal;

public record GradeRequest(
        String gradeName,
        BigDecimal gradePointRatio,
        Integer gradeCondition
){}