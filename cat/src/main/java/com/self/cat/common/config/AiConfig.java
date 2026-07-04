package com.self.cat.common.config;

import com.self.cat.model.ai.interfaces.CatAiAgent;
import com.self.cat.model.ai.interfaces.DatabaseMemoryStore;
import com.self.cat.model.ai.tools.PetServicesTool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Value("${ai.dashscope.api-key}")
    private String apiKey;

    @Value("${ai.dashscope.base-url}")
    private String baseUrl;

    @Value("${ai.dashscope.model}")
    private String modelName;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public StreamingChatLanguageModel streamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public CatAiAgent catAiAgent(ChatLanguageModel chatLanguageModel,
                                 DatabaseMemoryStore databaseMemoryStore,
                                 StreamingChatLanguageModel streamingModel,
                                 PetServicesTool petServicesTool) {

        return AiServices.builder(CatAiAgent.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(50)
                        .chatMemoryStore(databaseMemoryStore)
                        .build())
                .streamingChatLanguageModel(streamingModel)
                .tools(petServicesTool)
                .build();
    }

}
