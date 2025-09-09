package com.expensetracker.expense_service.Client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
@Component
public class FeignClientInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            System.out.println("FeignClientInterceptor: No request attributes");
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String authHeader = request.getHeader("Authorization");
        System.out.println("FeignClientInterceptor: Authorization header=" + authHeader);
        if (authHeader != null) {
            template.header("Authorization", authHeader);
        }
    }
}
