package com.self.cat.model.event.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName event
 */
@TableName(value ="event")
@Data
public class Event {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 事件名字
     */
    @TableField(value = "event_name")
    private String eventName;

    /**
     * 事件时间
     */
    @TableField(value = "event_time")
    private Date eventTime;

    /**
     * 事件内容
     */
    @TableField(value = "event_content")
    private String eventContent;

    /**
     * 事件关联的宠物
     */
    @TableField(value = "pet_id")
    private Integer petId;

    /**
     * 宠物名字
     */
    @TableField(value = "pet_name")
    private String petName;

    /**
     * 用户ID 是谁创建了这个事件
     */
    @TableField(value = "user_id")
    private Integer userId;

    /**
     * 是否完成？1完成0未完成
     */
    @TableField(value = "is_completed")
    private Integer isCompleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Event other = (Event) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getEventName() == null ? other.getEventName() == null : this.getEventName().equals(other.getEventName()))
            && (this.getEventContent() == null ? other.getEventContent() == null : this.getEventContent().equals(other.getEventContent()))
            && (this.getPetId() == null ? other.getPetId() == null : this.getPetId().equals(other.getPetId()))
            && (this.getPetName() == null ? other.getPetName() == null : this.getPetName().equals(other.getPetName()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getIsCompleted() == null ? other.getIsCompleted() == null : this.getIsCompleted().equals(other.getIsCompleted()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getEventName() == null) ? 0 : getEventName().hashCode());
        result = prime * result + ((getEventContent() == null) ? 0 : getEventContent().hashCode());
        result = prime * result + ((getPetId() == null) ? 0 : getPetId().hashCode());
        result = prime * result + ((getPetName() == null) ? 0 : getPetName().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getIsCompleted() == null) ? 0 : getIsCompleted().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", eventName=").append(eventName);
        sb.append(", eventContent=").append(eventContent);
        sb.append(", petId=").append(petId);
        sb.append(", petName=").append(petName);
        sb.append(", userId=").append(userId);
        sb.append(", isCompleted=").append(isCompleted);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append("]");
        return sb.toString();
    }
}