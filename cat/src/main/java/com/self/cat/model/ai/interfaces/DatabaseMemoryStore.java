package com.self.cat.model.ai.interfaces;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.self.cat.model.ai.domain.ChatRecord;
import com.self.cat.model.ai.domain.Conversation;
import com.self.cat.model.ai.service.ChatRecordService;
import com.self.cat.model.ai.service.ChatSummaryService;
import com.self.cat.model.ai.service.ConversationService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class DatabaseMemoryStore implements ChatMemoryStore {

    private final ChatRecordService chatRecordService;
    private final ConversationService conversationService;
    private final ChatSummaryService chatSummaryService;

    public DatabaseMemoryStore(ChatRecordService chatRecordService,
                               ConversationService conversationService,
                               ChatSummaryService chatSummaryService) {
        this.chatRecordService = chatRecordService;
        this.conversationService = conversationService;
        this.chatSummaryService = chatSummaryService;
    }



    /**
     * 当聊天记录超过 50 条时，你删除最旧的 20 条 不要删除！！！！不然聊天记录会丢失！！！！前端显示就会有问题
     * 你让 AI 总结那 20 条消息。
     * 你将这段文本存入数据库，角色标记为 SUMMARY。
     * 当你加载聊天记录时，你寻找 SUMMARY 记录。
     * 如果有多个总结，你只取最新的那一个。
     * 它和宠物数据结合在一起，变成一个全新且庞大的 SystemMessage。
     * 注意：需要异步执行
     * 每次用户发消息都执行一次超过50行就执行总结 并插入 没有超过就 跳过 直接return
     **/

    // LangChain4j calls this to read history
    // LangChain4j 调用这个来读取历史记录
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        log.info("getMessages memoryId:{}", memoryId);
        Long conversationId = (Long) memoryId;
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
        List<ChatMessage> result = new ArrayList<>();

        // 1. Get the pet data from your database (example data here)
        // 1. 从你的数据库获取宠物数据（此处为示例数据）
        Conversation conversation = conversationService.getById(conversationId);
        // TODO 拿到了宠物ID就去日程表中找对应宠物要做什么事情，但是我感觉，宠物之间跨事件好一点 例如A宠物知道B宠物要干嘛
        Long petId = conversation.getPetId();

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
                
                下面是你与用户过去对话的总结。
                
                使用这个记忆来理解聊天上下文。
                
                过去的记忆：
                        {chat_summary}
                
                不要提及你正在阅读总结。
                
                
                只需自然地与你的主人交谈。
                """);

        // 找到总结关键字 加入到data中 并复制到systemPrompt
        Map<String, Object> data = new HashMap<>();
        data.put("petType", petType);
        data.put("name", name);
        data.put("age", age);
        data.put("weight", weight);
        data.put("schedule", schedule);
        if (latestSummary != null) {
            data.put("chat_summary", latestSummary.getContent());
        }
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