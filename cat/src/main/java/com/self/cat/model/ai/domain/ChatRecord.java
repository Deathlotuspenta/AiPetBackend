package com.self.cat.model.ai.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * AI Chat Record Table
 * @TableName chat_record
 */
@TableName(value ="chat_record")
@Data
public class ChatRecord {
    /**
     * Primary key, unique ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Links to the conversation table
     */
    @TableField(value = "conversation_id")
    private Long conversationId;

    /**
     * Sender: user, assistant, or system
     */
    @TableField(value = "role")
    private String role;

    /**
     * The actual message content
     */
    @TableField(value = "content")
    private String content;

    /**
     * Number of tokens used
     */
    @TableField(value = "token_count")
    private Integer tokenCount;

    /**
     * Creation time
     */
    @TableField(value = "created_at")
    private Date createdAt;

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
        ChatRecord other = (ChatRecord) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getConversationId() == null ? other.getConversationId() == null : this.getConversationId().equals(other.getConversationId()))
            && (this.getRole() == null ? other.getRole() == null : this.getRole().equals(other.getRole()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getTokenCount() == null ? other.getTokenCount() == null : this.getTokenCount().equals(other.getTokenCount()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getConversationId() == null) ? 0 : getConversationId().hashCode());
        result = prime * result + ((getRole() == null) ? 0 : getRole().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getTokenCount() == null) ? 0 : getTokenCount().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", conversationId=").append(conversationId);
        sb.append(", role=").append(role);
        sb.append(", content=").append(content);
        sb.append(", tokenCount=").append(tokenCount);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}