package com.nhnacademy.frontserver.common;

import com.nhnacademy.frontserver.infra.argumentResolver.CustomArgumentResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PageArgumentResolver implements CustomArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Page.class.equals(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory)
            throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return Page.of(getPageNumber(request),getPageSize(request));
    }

    private int getPageNumber(HttpServletRequest request) {
        String parameter = request.getParameter("pageNumber");
        if(parameter == null) {
            return 0;
        }
        return Integer.parseInt(parameter);
    }
    private int getPageSize(HttpServletRequest request) {
        String parameter = request.getParameter("pageSize");
        if(parameter == null) {
            return 10;
        }
        return Integer.parseInt(parameter);
    }
}
