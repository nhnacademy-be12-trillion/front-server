package com.nhnacademy.frontserver.Auth.dto;


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
