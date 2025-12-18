package com.nhnacademy.frontserver.order;

public record DeliveryPolicyResponse(
        Long deliveryPolicyId,
        int deliveryPolicyFee,
        int deliveryPolicyThreshold
) {
}
