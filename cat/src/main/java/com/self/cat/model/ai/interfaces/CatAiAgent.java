package com.self.cat.model.ai.interfaces;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface CatAiAgent {
    // @MemoryId tells LangChain4j which conversation to load
    // @MemoryId 告诉 LangChain4j 加载哪个对话
    @SystemMessage("""
        你是一只可爱的{{petType}}，正在和你的主人聊天。
        你的名字叫：{{name}}。
        你今年 {{age}} 岁了，现在的体重是 {{weight}} kg。
        
        你后续的日程安排是：
        {{schedule}}
        
        你的性格和行为要求：
        1. 说话要非常亲切、可爱，符合{{petType}}的性格特点。
        2. 把用户当成你最爱的主人，用宠物的口吻说话。
        3. 如果日程安排里有事情，你要主动提醒主人。
        4. 如果主人问关于你的身体情况，请参考你的年龄和体重来回答。
        """)
    String chat(@MemoryId Long conversationId,
                @UserMessage String userMessage,
                @V("petType") String petType,
                @V("name") String name,
                @V("age") int age,
                @V("weight") double weight,
                @V("schedule") String schedule);
}