package com.nhnacademy.frontserver.point;

import java.time.LocalDateTime;

public record PointHistoryResponse(
        Long pointHistoryId,
        String reason,
        Integer amount, // 변동 금액
        Integer currentBalance, // 잔액
        LocalDateTime transactionAt
) {
}
