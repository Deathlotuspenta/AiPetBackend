package com.self.cat.model.owner.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavePetInformationDto {
    /**
     * 宠物名字
     */
    @Schema(description = "宠物名字",example = "煤球")
    private String petName;
    /**
     * 宠物出生日期
     */
    @Schema(description = "宠物出生日期",example = "2026")
    private Date petAge;

    /**
     * 宠物种类
     */
    @Schema(description = "宠物种类",example = "猫猫")
    private String petType;

    /**
     * 宠物品种
     */
    @Schema(description = "宠物品种",example = "暹罗")
    private String petVariety;

    /**
     * 宠物品种
     */
    @Schema(description = "宠物体重(kg)",example = "3.6kg")
    private Double petWeight;

    /**
     * 宠物品种
     */
    @Schema(description = "宠物性别",example = "母")
    private String petSex;

    /**
     * 宠物品种
     */
    @Schema(description = "宠物头像",example = "🐱")
    private String petAvatar;
}
