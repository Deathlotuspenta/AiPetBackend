package com.self.cat.model.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.self.cat.model.ai.domain.Conversation;
import com.self.cat.model.ai.service.ConversationService;
import com.self.cat.model.ai.mapper.ConversationMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【conversation(AI Chat Conversation Table)】的数据库操作Service实现
* @createDate 2026-05-21 15:48:01
*/
@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
    implements ConversationService{

}




