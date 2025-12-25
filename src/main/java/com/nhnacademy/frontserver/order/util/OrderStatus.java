/*
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 * + Copyright 2025. NHN Academy Corp. All rights reserved.
 * + ...
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 */

package com.nhnacademy.frontserver.order.util;

public enum OrderStatus {
    PENDING("결제 대기"),
    COMPLETED("결제 완료"),
    CANCELED("주문 취소"),
    FAILED("주문 실패"),

    // [추가] 백엔드에서 넘어오는 값 추가
    CREATION_FAILED("주문 생성 실패");

    private final String title;

    OrderStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}