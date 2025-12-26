package com.nhnacademy.frontserver.auth.dto;


public record TokenResponse(
        String accessToken,
        String refreshToken
) {
    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
