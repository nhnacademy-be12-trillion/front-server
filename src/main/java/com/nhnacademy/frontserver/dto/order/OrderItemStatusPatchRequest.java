package com.nhnacademy.frontserver.dto.order.util;

import com.nhnacademy.order.orderitem.domain.OrderItemStatus;

public record OrderItemStatusPatchRequest(
    OrderItemStatus status
) {}
