package com.self.cat.model.owner.controller;

import com.self.cat.common.enums.ResultCode;
import com.self.cat.common.exception.UserException;
import com.self.cat.common.http.HttpResult;
import com.self.cat.model.owner.domain.User;
import com.self.cat.model.owner.domain.dto.LoginDto;
import com.self.cat.model.owner.domain.dto.UserRegisterDto;
import com.self.cat.model.owner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理",description = "用户管理相关")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/userRegister")
    @Operation(summary = "用户注册")
    public HttpResult<String> userRegister(@RequestBody UserRegisterDto user) {
        String confirmPassword = user.getConfirmPassword();
        String password = user.getPassword();
        if (!confirmPassword.equals(password)) {
            throw new UserException(ResultCode.CONFIRM_ERROR.getCode(), ResultCode.CONFIRM_ERROR.getMessage());
        }

        boolean result = userService.register(user);
        if (result) {
            return HttpResult.success("注册成功");
        }

        return HttpResult.error(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage());
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public HttpResult<String> login(@RequestBody LoginDto login) {
        log.info("登录控制器被执行");
        String phone = login.getPhone();
        String password = login.getPassword();

        // 验证用户是否存在
        User user = userService.login(phone,password);
        if (user == null) {
            return HttpResult.error(ResultCode.LOGIN_USER.getCode(), ResultCode.LOGIN_USER.getMessage());
        }
        String jwt = userService.createJwt(user);
        // 1. Create a unique key for Redis (e.g., "token:12345")
// 1. 为 Redis 创建一个唯一的键（例如："token:12345"）
        String redisKey = "token:" + user.getId();

// 2. Save to Redis with an expiration time (e.g., 30 days)
// 2. 存入 Redis 并设置过期时间（例如：30 天）
        redisTemplate.opsForValue().set(redisKey, jwt, 30, TimeUnit.DAYS);

        return HttpResult.success(jwt);
    }
}
