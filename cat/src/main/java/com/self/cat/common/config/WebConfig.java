package com.self.cat.common.config;

import com.self.cat.common.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // Add your new interceptor.
        // 添加你的新拦截器。
        registry.addInterceptor(loginInterceptor)

                // Tell it to check all web paths.
                // 告诉它检查所有的网络路径。
                .addPathPatterns("/**")

                // Tell it to ignore the login path.
                // 告诉它忽略登录路径。
                .excludePathPatterns("/user/login","/user/userRegister","/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html",
                        "/swagger-resources/**",
                        "/webjars/**");
    }
}