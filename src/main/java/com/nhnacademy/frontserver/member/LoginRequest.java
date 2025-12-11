package com.nhnacademy.frontserver.member;

public record LoginRequest(
        String memberEmail,
        String memberPassword
) {}