package com.self.cat.model.mypet.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 
 * @TableName pet
 */
@TableName(value ="pet")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 宠物主人ID
     */
    @TableField(value = "pet_master_id")
    private Integer petMasterId;

    /**
     * 宠物名字
     */
    @TableField(value = "pet_name")
    private String petName;

    /**
     * 宠物头像
     */
    @TableField(value = "pet_avatar")
    private String petAvatar;

    /**
     * 宠物出生日期
     */
    @TableField(value = "pet_age")
    private Date petAge;

    /**
     * 宠物种类
     */
    @TableField(value = "pet_type")
    private String petType;

    /**
     * 宠物体重
     */
    @TableField(value = "pet_weight")
    private Double petWeight;

    /**
     * 宠物品种
     */
    @TableField(value = "pet_variety")
    private String petVariety;

    /**
     * 创建日期
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新日期
     */
    @TableField(value = "update_time")
    private Date updateTime;
}