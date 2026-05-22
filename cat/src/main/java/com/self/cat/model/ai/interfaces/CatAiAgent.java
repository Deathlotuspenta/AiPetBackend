package com.self.cat.model.ai.interfaces;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CatAiAgent {
    // @MemoryId tells LangChain4j which conversation to load
    // @MemoryId 告诉 LangChain4j 加载哪个对话
    String chat(@MemoryId Long conversationId,
                @UserMessage String userMessage);
}