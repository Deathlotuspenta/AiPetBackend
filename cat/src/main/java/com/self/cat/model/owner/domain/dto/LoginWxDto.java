package com.self.cat.model.owner.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginWxDto {
    public String code;
    public String phoneCode;
}
