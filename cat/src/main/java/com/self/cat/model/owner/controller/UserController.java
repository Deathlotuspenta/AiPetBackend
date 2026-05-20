package com.self.cat.model.owner.controller;

import com.self.cat.common.enums.ResultCode;
import com.self.cat.common.exception.UserException;
import com.self.cat.common.http.HttpResult;
import com.self.cat.model.owner.domain.dto.UserRegisterDto;
import com.self.cat.model.owner.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理",description = "用户管理相关")
public class UserController {

    @Autowired
    private UserService userService;

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

}
