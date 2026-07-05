package com.self.cat.model.ai.interfaces;


import dev.langchain4j.service.*;

public interface CatAiAgent {
    // @MemoryId tells LangChain4j which conversation to load
    // @MemoryId 告诉 LangChain4j 加载哪个对话

    /**
     * SSE 流式聊天 — 返回 TokenStream，逐字推送到前端
     * 适用：callContainer 可用时（微信真机 + 云托管已绑定）
     */
    TokenStream chat(@MemoryId Long conversationId,
                     @UserMessage String userMessage
                );

    /**
     * 非流式聊天 — 返回完整 String，一次性应答
     * 适用：devtools 兜底 / callContainer 不可用时
     * LangChain4j 自动使用同步 ChatLanguageModel（OpenAiChatModel）调用
     */
    String chatSync(@MemoryId Long conversationId,
                    @UserMessage String userMessage
                );
}
