package com.self.cat.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取jwt
        String authorization = request.getHeader("Authorization");

        // 2. Is there no token? / 如果没有令牌？
        if (authorization == null || authorization.isEmpty()) {
            // 1. Set Status to 401
            // 1. 设置状态码为 401
            response.setStatus(401); // 401 means "Unauthorized" / 401 表示“未授权”

            // 2. Set Content Type (UTF-8 is important for Chinese characters)
            // 2. 设置内容类型（UTF-8 对于中文字符很重要）
            response.setContentType("application/json;charset=UTF-8");

            // 3. Create your JSON error message
            // 3. 创建你的 JSON 错误消息
            String jsonError = "{\"code\": 401, \"message\": \"No token found. Please log in.\"}";

            // 4. Write it to the response
            // 4. 将其写入响应
            response.getWriter().write(jsonError);

            // Stop the request
            // 停止请求
            return false;
        }

        // Return true to pass. Return false to block.
        // 返回 true 则通过。返回 false 则拦截。
        return true;
    }
}
