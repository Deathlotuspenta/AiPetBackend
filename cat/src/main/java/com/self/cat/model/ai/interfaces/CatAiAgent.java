package com.self.cat.model.ai.interfaces;


import dev.langchain4j.service.*;

public interface CatAiAgent {
    // @MemoryId tells LangChain4j which conversation to load
    // @MemoryId 告诉 LangChain4j 加载哪个对话
    TokenStream chat(@MemoryId Long conversationId,
                     @UserMessage String userMessage
                );
}