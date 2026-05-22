package com.self.cat.common.config;

import com.self.cat.model.ai.interfaces.CatAiAgent;
import com.self.cat.model.ai.interfaces.DatabaseMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey("sk-7a47752ca37c4e0aa1d59ce523d5312a")
                .modelName("qwen3.6-flash")
                .logRequests(true)  // 1. Log what goes out (记录发出的内容)
                .logResponses(true) // 2. Log what comes back (记录返回的内容)
                .build();
    }

    @Bean
    public CatAiAgent catAiAgent(ChatLanguageModel chatLanguageModel,
                                 DatabaseMemoryStore databaseMemoryStore) {

        return AiServices.builder(CatAiAgent.class)
                .chatLanguageModel(chatLanguageModel)
                // Set up the memory window
                // 设置记忆窗口
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20) // Only keep the last 20 messages (只保留最近20条消息)
                        .chatMemoryStore(databaseMemoryStore) // Use your database (使用你的数据库)
                        .build())
                .build();
    }

}
