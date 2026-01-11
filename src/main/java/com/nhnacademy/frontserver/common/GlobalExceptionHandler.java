package com.nhnacademy.frontserver.common;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({
            FeignException.BadRequest.class,
            FeignException.NotFound.class,
            FeignException.Conflict.class,
            FeignException.MethodNotAllowed.class,

    })
    public String  handleFeignException(Exception exception, HttpServletRequest request) {
        log.info("feign error");
        log.info("exception: {}", exception.getMessage());
        log.info("requestURL: {}", request.getRequestURL().toString());
        String referer = request.getHeader("Referer");
        log.info("requestURL: {}",request);
        log.info("=====", referer);

        // 이전 페이지로 리다이렉트 (없으면 홈으로)
        return "redirect:" + (referer != null ? referer : "/");
    }
}
