package com.nhnacademy.frontserver.order;

public record DeliveryPolicyUpdateRequest(
        int deliveryPolicyFee,
        int deliveryPolicyThreshold
) {}
