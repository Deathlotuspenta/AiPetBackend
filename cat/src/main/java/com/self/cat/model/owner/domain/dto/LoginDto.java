package com.self.cat.model.owner.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @NotEmpty
    @Schema(description = "手机号", example = "18323042309")
    private String phone;

    @NotEmpty
    @Schema(description = "密码",example = "1234567")
    private String password;

}
