package com.self.cat.model.ai;

import com.self.cat.common.enums.ResultCode;
import com.self.cat.common.http.HttpResult;
import com.self.cat.model.ai.domain.ChatRecord;
import com.self.cat.model.ai.domain.Conversation;
import com.self.cat.model.ai.domain.dto.MessageDto;
import com.self.cat.model.ai.interfaces.CatAiAgent;
import com.self.cat.model.ai.service.ChatRecordService;
import com.self.cat.model.ai.service.ConversationService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/AiController")
@Tag(name = "Ai接口",description = "Ai相关接口，用于聊天 or 记录事情")
public class AiController {

    private final ConversationService conversationService;
    // You need a service to save single messages
    private final ChatRecordService chatRecordService;
    // This is the LangChain4j model
    private final ChatLanguageModel chatLanguageModel;

    private final CatAiAgent catAiAgent;

    // Inject the services in the constructor
    public AiController(ConversationService conversationService,
                          ChatRecordService chatRecordService,
                          ChatLanguageModel chatLanguageModel,
                        CatAiAgent catAiAgent) {
        this.conversationService = conversationService;
        this.catAiAgent = catAiAgent;
        this.chatRecordService = chatRecordService;
        this.chatLanguageModel = chatLanguageModel;
    }

    @GetMapping("/startConversation")
    @Operation(summary = "开启会话")
    public HttpResult<Long> startConversation() {
        // 先创建会话
        Conversation conversation = new Conversation();
        conversation.setTitle(null);
        // TODO
        conversation.setUserId(1L);
        Date date = new Date();
        conversation.setCreatedAt(date);
        conversation.setUpdatedAt(date);
        conversation.setIsDeleted(0);

        conversationService.save(conversation);
        return HttpResult.success(conversation.getId());
    }

    @PostMapping("/chat")
    @Operation(summary = "聊天")
    public HttpResult<String> chat(@RequestBody MessageDto message) {
        String userMessage = message.getMessage();

        // 查找会话是否存在
        Long conversationId = message.getConversationId();
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation == null) {
            return HttpResult.error(ResultCode.ERROR.getCode(), "会话不存在,请重新新建会话");
        }
        // 1. Get the pet data from your database (example data here)
        // 1. 从你的数据库获取宠物数据（此处为示例数据）
        String petType = "金毛犬";
        String name = "大黄";
        int age = 3;
        double weight = 25.5;
        String schedule = "下午5点要去公园散步，晚上吃牛肉罐头。";

        // 2. Pass the data directly to the agent
        // 2. 将数据直接传递给代理
        String aiAnswer = catAiAgent.chat(
                conversationId,
                userMessage,
                petType,
                name,
                age,
                weight,
                schedule
        );

        return HttpResult.success(aiAnswer);
    }

}
