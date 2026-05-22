package com.self.cat.model.ai.interfaces;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.self.cat.model.ai.domain.ChatRecord;
import com.self.cat.model.ai.domain.Conversation;
import com.self.cat.model.ai.service.ChatRecordService;
import com.self.cat.model.ai.service.ConversationService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DatabaseMemoryStore implements ChatMemoryStore {

    private final ChatRecordService chatRecordService;
    private final ConversationService conversationService;

    public DatabaseMemoryStore(ChatRecordService chatRecordService,
                               ConversationService conversationService) {
        this.chatRecordService = chatRecordService;
        this.conversationService = conversationService;
    }

    // LangChain4j calls this to read history
    // LangChain4j 调用这个来读取历史记录
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        Long conversationId = (Long) memoryId;

        LambdaQueryWrapper<ChatRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatRecord::getConversationId, conversationId);
        List<ChatRecord> dbRecords = chatRecordService.list(queryWrapper);
        List<ChatMessage> result = new ArrayList<>();

        // 1. Get the pet data from your database (example data here)
        // 1. 从你的数据库获取宠物数据（此处为示例数据）
        String petType = "金毛犬";
        String name = "大黄";
        int age = 3;
        double weight = 25.5;
        String schedule = "下午5点要去公园散步，晚上吃牛肉罐头。";

        PromptTemplate promptTemplate = PromptTemplate.from("""
        你是一只可爱的{{petType}}，正在和你的主人聊天。
        你的名字叫：{{name}}。
        你今年 {{age}} 岁了，现在的体重是 {{weight}} kg。
        
        包括主人和你的后续的日程安排是：
        {{schedule}}
        
        你的性格和行为要求：
        1. 说话要非常亲切、可爱，符合{{petType}}的性格特点。
        2. 把用户当成你最爱的主人，用宠物的口吻说话。
        3. 如果日程安排里有事情，你要主动提醒主人。
        4. 如果主人问关于你的身体情况，请参考你的年龄和体重来回答。
  
        """);

        Map<String, Object> data = new HashMap<>();
        data.put("petType", petType);
        data.put("name", name);
        data.put("age", age);
        data.put("weight", weight);
        data.put("schedule", schedule);
//        data.put("userMessage", userMessage);
        String finalPrompt = promptTemplate.apply(data).text();
        result.add(new SystemMessage(finalPrompt));

        if (dbRecords == null || dbRecords.isEmpty()) {
            return result;
        }


        // 2. Change database records to LangChain4j messages
        // 2. 将数据库记录转换为 LangChain4j 消息
        for (ChatRecord record : dbRecords) {
            if ("USER".equals(record.getRole())) {
                result.add(new UserMessage(record.getContent()));
            } else if ("AI".equals(record.getRole())) {
                result.add(new AiMessage(record.getContent()));
            }
        }

        return result;
    }

    // LangChain4j calls this to save new messages
    // LangChain4j 调用这个来保存新消息
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        Long conversationId = (Long) memoryId;
        ChatMessage lastMessage = messages.get(messages.size() - 1);

        ChatRecord record = new ChatRecord();
        record.setConversationId(conversationId);
        record.setCreatedAt(new Date());

        // 1. Check the type and cast the object
        // 1. 检查类型并转换对象
        if (lastMessage instanceof UserMessage userMessage) {
            record.setContent(userMessage.singleText()); // Now text() works (现在 text() 可以工作了)
            record.setRole("USER");

        } else if (lastMessage instanceof AiMessage aiMessage) {
            record.setContent(aiMessage.text()); // Now text() works (现在 text() 可以工作了)
            record.setRole("AI");

        } else {
            // Ignore other types like SystemMessage for now
            // 暂时忽略像 SystemMessage 这样的其他类型
            return;
        }

        // 检查会话标题是否为空
        Conversation conversation = conversationService.getById(conversationId);

        boolean isSaveTitle = conversation != null
                && (conversation.getTitle() == null || conversation.getTitle().isEmpty())
                && "USER".equals(record.getRole());

        if (isSaveTitle) {
            conversation.setTitle(record.getContent());

            conversationService.updateById(conversation);
        }


        chatRecordService.save(record);
    }

    @Override
    public void deleteMessages(Object memoryId) {

    }
}