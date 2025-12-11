package com.nhnacademy.frontserver.order;

public record NonMemberOrderCancelRequest(
    String nonMemberPassword
) {}
