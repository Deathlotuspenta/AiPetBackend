package com.self.cat.model.owner.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePetInformationDto {
    /**
     * 宠物名字
     */
    private String petName;

    /**
     * 宠物头像
     */
    private String petAvatar;

    /**
     * 宠物出生日期
     */
    private Date petAge;

    /**
     * 宠物种类
     */
    private String petType;

    /**
     * 宠物体重
     */
    private double petWeight;

    /**
     * 宠物品种
     */
    private String petVariety;

    /**
     * 宠物性别
     */
    @Schema(description = "宠物性别",example = "母")
    private String petSex;

}
