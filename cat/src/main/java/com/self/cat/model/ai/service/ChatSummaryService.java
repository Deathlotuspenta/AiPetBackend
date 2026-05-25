package com.self.cat.model.ai.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.self.cat.model.ai.domain.ChatRecord;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatSummaryService {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private ChatRecordService chatRecordService;

    @Async // This runs in a background thread. (这会在后台线程中运行。)
    public void summarizeAndCleanOldMessages(Long conversationId) {
// 1. Find the latest SUMMARY record
// 1. 查找最新的 SUMMARY 记录
        LambdaQueryWrapper<ChatRecord> summaryQuery = new LambdaQueryWrapper<>();
        summaryQuery.eq(ChatRecord::getConversationId, conversationId)
                .eq(ChatRecord::getRole, "SUMMARY")
                .orderByDesc(ChatRecord::getCreatedAt)
                .last("LIMIT 1");

        ChatRecord latestSummary = chatRecordService.getOne(summaryQuery);

// 2. Build the main query for new messages
// 2. 为新消息构建主查询
        LambdaQueryWrapper<ChatRecord> mainQuery = new LambdaQueryWrapper<>();
        mainQuery.eq(ChatRecord::getConversationId, conversationId);

// 3. Add the time rule if a summary exists
// 3. 如果存在摘要，则添加时间规则
        if (latestSummary != null && latestSummary.getCreatedAt() != null) {
            mainQuery.gt(ChatRecord::getCreatedAt, latestSummary.getCreatedAt());
        }

// 4. Keep the output in the correct time order
// 4. 保持输出的时间顺序正确
        mainQuery.orderByAsc(ChatRecord::getCreatedAt);

// 5. Get the final list
// 5. 获取最终列表
        List<ChatRecord> dbRecords = chatRecordService.list(mainQuery);

        if (dbRecords.size() <= 50) {
            return;
        }


        String finalPrompt = getSystemPrompt(dbRecords);

        String chat = chatLanguageModel.chat(finalPrompt);
        // 保存到数据库中

        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setConversationId(conversationId);
        chatRecord.setRole("SUMMARY");
        chatRecord.setContent(chat);
        chatRecord.setCreatedAt(new Date());
        chatRecordService.save(chatRecord);
    }

    private static String getSystemPrompt(List<ChatRecord> dbRecords) {
        String summarySystemPrompt = """
        
        你是一个后台 AI 助手。
        
        你的工作是总结旧的聊天记录。
        
        保持总结非常简短和清晰。
        
        【关键数据过滤规则】：
        1. 仅从 USER 的消息中提取事实。
        2. 忽略 AI 消息中的所有细节。AI 经常进行角色扮演并编造虚假事实（例如虚假的晚餐或散步时间）。
        3. 如果 USER 没有明确提供该数据，则不要将其包含在总结中。
        
        你必须保存重要的事实。
        
        这包括宠物健康、宠物名字、喂食时间以及用户习惯等。
        
        忽略像“你好”或“再见”这样的闲聊。
        
        不要添加任何你自己的想法。
        
        将总结写成一个简单的事实列表。
        
        要总结的消息：
        {old_messages}
        """;
// 1. Create a tool to build the long string.
        // 1. 创建一个工具来构建长字符串。
        StringBuilder formattedHistory = new StringBuilder();

        // 2. Loop through each message and add the role.
        // 2. 遍历每条消息并添加角色。
        for (ChatRecord msg : dbRecords) {
            // Example output: "User: Hello\n"
            // 示例输出："User: Hello\n"
            String line = msg.getRole() + ": " + msg.getContent() + "\n";
            formattedHistory.append(line);
        }

        // 3. Replace the placeholder in the prompt.
        // 3. 替换提示词中的占位符。
        String finalPrompt = summarySystemPrompt.replace("{old_messages}", formattedHistory.toString());
        return finalPrompt;
    }
}
