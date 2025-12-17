package com.nhnacademy.frontserver.Auth.dto;

public record LoginRequest(
        String memberEmail,
        String memberPassword
) {}
