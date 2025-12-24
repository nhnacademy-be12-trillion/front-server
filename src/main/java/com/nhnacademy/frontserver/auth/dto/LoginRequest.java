package com.nhnacademy.frontserver.auth.dto;

public record LoginRequest(
        String memberEmail,
        String memberPassword
) {}
