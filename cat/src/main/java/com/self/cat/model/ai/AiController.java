package com.self.cat.model.ai;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.self.cat.common.enums.ResultCode;
import com.self.cat.common.http.HttpResult;
import com.self.cat.common.utils.UserContext;
import com.self.cat.model.ai.domain.ChatRecord;
import com.self.cat.model.ai.domain.Conversation;
import com.self.cat.model.ai.domain.dto.MessageDto;
import com.self.cat.model.ai.interfaces.CatAiAgent;
import com.self.cat.model.ai.service.ChatRecordService;
import com.self.cat.model.ai.service.ChatSummaryService;
import com.self.cat.model.ai.service.ConversationService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.TokenStream;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/AiController")
@Tag(name = "Ai接口",description = "Ai相关接口，用于聊天 or 记录事情")
public class AiController {

    private final ConversationService conversationService;
    // You need a service to save single messages
    private final ChatRecordService chatRecordService;
    // This is the LangChain4j model
    private final ChatLanguageModel chatLanguageModel;

    private final ChatSummaryService chatSummaryService;

    private final CatAiAgent catAiAgent;

    // Inject the services in the constructor
    public AiController(ConversationService conversationService,
                          ChatRecordService chatRecordService,
                          ChatLanguageModel chatLanguageModel,
                        CatAiAgent catAiAgent,
                        ChatSummaryService chatSummaryService) {
        this.conversationService = conversationService;
        this.chatSummaryService = chatSummaryService;
        this.catAiAgent = catAiAgent;
        this.chatRecordService = chatRecordService;
        this.chatLanguageModel = chatLanguageModel;
    }

    @GetMapping("/startConversation/{petId}")
    @Operation(summary = "开启会话")
    public HttpResult<Long> startConversation(@PathVariable Long petId) {

        String id = UserContext.get("id");
        // 查找这个宠物的会话如存在就返回该宠物的会话
        LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<Conversation>();
        queryWrapper.eq(Conversation::getPetId, petId).last("limit 1");

        Conversation one = conversationService.getOne(queryWrapper);

        if (one != null) {
            return HttpResult.success(one.getId());
        }


        // 先创建会话
        Conversation conversation = new Conversation();
        conversation.setPetId(petId);
        conversation.setTitle(null);
        conversation.setUserId(Long.valueOf(id));
        Date date = new Date();
        conversation.setCreatedAt(date);
        conversation.setUpdatedAt(date);
        conversation.setIsDeleted(0);

        conversationService.save(conversation);
        return HttpResult.success(conversation.getId());
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天")
    public SseEmitter chat(@RequestBody MessageDto message) {
        log.info("开启AI聊天！");
        String userMessage = message.getMessage();
        Long conversationId = message.getConversationId();

        // 2. Check if conversation exists
        // 2. 查找会话是否存在
        Conversation conversation = conversationService.getById(conversationId);
        if (conversation == null) {
            // If error, we still return SseEmitter but close it with an error message
            // 如果出错，我们仍然返回 SseEmitter，但用错误消息关闭它
            SseEmitter errorEmitter = new SseEmitter();
            errorEmitter.completeWithError(new RuntimeException("会话不存在,请重新新建会话"));
            return errorEmitter;
        }

        // 3. Create SseEmitter (Timeout is set to 0, which means infinite)
        // 3. 创建 SseEmitter（超时设置为 0，这意味着无限期）
        SseEmitter emitter = new SseEmitter(0L);

        // 4. Get the TokenStream from AI
        // 4. 从 AI 获取 TokenStream
        TokenStream tokenStream = catAiAgent.chat(conversationId, userMessage);

        log.info("开始发送/chat请求");

        // 5. Handle the stream events
        // 5. 处理流事件a
        tokenStream
                .onPartialResponse(token -> {
                    // When a new word arrives, send it to the frontend
                    // 当新词到达时，将其发送给前端
                    try {
                        emitter.send(token);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                })
                .onCompleteResponse(response -> {
                    // When the AI finishes talking, close the stream
                    // 当 AI 完成讲话时，关闭流
                    emitter.complete();

                    // IMPORTANT: Trigger the summary check HERE
                    // 重要提示：在这里触发总结检查
                    chatSummaryService.summarizeAndCleanOldMessages(conversationId);
                })
                .onError(error -> {
                    // If AI fails, close stream with error
                    // 如果 AI 失败，用错误关闭流
                    emitter.completeWithError(error);
                })
                .start(); // Start the stream (启动流)

        // 6. Return the emitter immediately
        // 6. 立即返回 emitter
        return emitter;
    }

}
