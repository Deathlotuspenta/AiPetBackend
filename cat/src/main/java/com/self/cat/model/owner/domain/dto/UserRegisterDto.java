package com.self.cat.model.owner.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRegisterDto {

    @Schema(description = "用户名",example="小美")
    private String username;

    @Schema(description = "手机号",example = "18323042309")
    private String Phone;

    @Schema(description = "密码" , example = "1234567")
    private String password;

    @Schema(description = "确认密码",example = "1234567")
    private String confirmPassword;
}
