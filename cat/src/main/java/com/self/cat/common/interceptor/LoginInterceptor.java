package com.self.cat.common.interceptor;

import com.self.cat.common.utils.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取jwt
        String authorization = request.getHeader("Authorization");

        // 2. Is there no token? / 如果没有令牌？
        if (authorization == null || authorization.isEmpty()) {
            return sendErrorResponse(response, "Invalid or expired token. Please log in again.");
        }

        // 解析JWT
        // Remove "Bearer " prefix if it exists
        // 如果存在 "Bearer " 前缀，请将其删除
        String token = authorization;
        if (authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }

        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            // Use the secret key to read the token
            // 使用密钥读取令牌
            Claims claims = Jwts.parserBuilder() // Use parserBuilder() for new JJWT versions / 新版 JJWT 使用 parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // The token is good. You can read user data here if needed.
            // 令牌是好的。如果需要，你可以从这里读取用户数据。
            // String username = claims.getSubject();
            String phone = claims.get("phone").toString();
            String username = claims.get("username").toString();
            String id = claims.get("id").toString();
            // 存入线程中
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("id",id);
            userInfo.put("phone", phone);
            userInfo.put("username", username);

            UserContext.set(userInfo);


        } catch (Exception e) {
            e.printStackTrace();
            return sendErrorResponse(response, "Invalid or expired token. Please log in again.");
        }

        // Return true to pass. Return false to block.
        // 返回 true 则通过。返回 false 则拦截。
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    private boolean sendErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        String jsonError = "{\"code\": 401, \"message\": \"" + message + "\"}";
        response.getWriter().write(jsonError);
        return false;
    }
}
