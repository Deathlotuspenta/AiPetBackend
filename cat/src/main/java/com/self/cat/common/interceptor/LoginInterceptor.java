package com.self.cat.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("1. preHandle: Checking the request.");

        // Return true to pass. Return false to block.
        // 返回 true 则通过。返回 false 则拦截。
        return true;
    }
}
