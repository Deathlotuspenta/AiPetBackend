package com.self.cat.common.config;

import com.self.cat.model.ai.interfaces.CatAiAgent;
import com.self.cat.model.ai.interfaces.DatabaseMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey("sk-96280128c4384cdb9e5753fdcc458773")
                .modelName("qwen3.6-flash")
                .logRequests(true)  // 1. Log what goes out (记录发出的内容)
                .logResponses(true) // 2. Log what comes back (记录返回的内容)
                .build();
    }

    @Bean
    public StreamingChatLanguageModel streamingChatModel() {
        // Build and return your streaming model here.
        // 在这里构建并返回您的流式模型。

        // This is an OpenAI example. Change it if you use a different AI provider.
        // 这是一个 OpenAI 的示例。如果您使用其他 AI 提供商，请更改它。
        return OpenAiStreamingChatModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey("sk-96280128c4384cdb9e5753fdcc458773")
                .modelName("qwen3.6-flash")
                .logRequests(true)  // 1. Log what goes out (记录发出的内容)
                .logResponses(true) // 2. Log what comes back (记录返回的内容)
                .build();
    }

    @Bean
    public CatAiAgent catAiAgent(ChatLanguageModel chatLanguageModel,
                                 DatabaseMemoryStore databaseMemoryStore,
                                 StreamingChatLanguageModel streamingModel) {

        return AiServices.builder(CatAiAgent.class)
                .chatLanguageModel(chatLanguageModel)
                // Set up the memory window
                // 设置记忆窗口
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(50) // Only keep the last 20 messages (只保留最近20条消息)
                        .chatMemoryStore(databaseMemoryStore) // Use your database (使用你的数据库)
                        .build())
                .streamingChatLanguageModel(streamingModel) // THIS IS REQUIRED FOR TokenStream
                .build();
    }

}
