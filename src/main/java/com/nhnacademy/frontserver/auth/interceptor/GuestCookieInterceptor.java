package com.nhnacademy.frontserver.auth.interceptor;

import com.nhnacademy.frontserver.auth.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class GuestCookieInterceptor implements HandlerInterceptor {

    // guestId 없으면 새로 발급
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String guestId = CookieUtils.getCookieValue(request, "guestId");

        if (guestId == null) {
            String newGuestId = UUID.randomUUID().toString();
            ResponseCookie cookie = CookieUtils.createHttpOnlyCookie("guestId", newGuestId, 60 * 60 * 24 * 7);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
        return true;
    }
}