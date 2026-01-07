package com.nhnacademy.frontserver.common;

import com.nhnacademy.frontserver.auth.dto.TokenResponse;
import com.nhnacademy.frontserver.auth.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class Token {
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String RE_ISSUE = "reissue";

    public static String getAccessToken(HttpServletRequest request) {
        if(request.getAttribute(ACCESS_TOKEN) == null) {
            return getCookieValue(request, ACCESS_TOKEN);
        }
        return request.getAttribute(ACCESS_TOKEN).toString();
    }

    public static String getRefreshToken(HttpServletRequest request) {
        if(request.getAttribute(REFRESH_TOKEN) == null) {
            return getCookieValue(request, REFRESH_TOKEN);
        }
        return request.getAttribute(REFRESH_TOKEN).toString();
    }
    public static boolean canReissue(HttpServletRequest request) {
        return getRefreshToken(request)!=null&&request.getAttribute(RE_ISSUE)==null;
    }
    public static void reissue(HttpServletRequest request,HttpServletResponse response,TokenResponse tokenResponse) {
        request.setAttribute(RE_ISSUE,true);
        issue(request, response, tokenResponse);
    }
    public static void issue(HttpServletRequest request,HttpServletResponse response,TokenResponse tokenResponse) {
        saveAccessToken(request,tokenResponse.getAccessToken());
        addCookies(response,tokenResponse);
    }
    private static void saveAccessToken(HttpServletRequest request,String newAccessToken) {
        request.setAttribute(ACCESS_TOKEN,newAccessToken);
    }

    private static String getCookieValue(HttpServletRequest request, String cookieName) {
        if(request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .map(Cookie::getValue)
                .orElse(null);
    }

    private static void addCookies(HttpServletResponse response, TokenResponse tokens) {
        ResponseCookie accessCookie = CookieUtils.createHttpOnlyCookie(ACCESS_TOKEN, tokens.getAccessToken(), 60 * 30);
        ResponseCookie refreshCookie = CookieUtils.createHttpOnlyCookie(REFRESH_TOKEN, tokens.getRefreshToken(), 60 * 60 * 24 * 7);

        // 응답 헤더에 추가
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}
