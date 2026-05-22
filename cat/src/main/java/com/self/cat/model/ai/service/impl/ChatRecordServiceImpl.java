package com.self.cat.model.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.self.cat.model.ai.domain.ChatRecord;
import com.self.cat.model.ai.service.ChatRecordService;
import com.self.cat.model.ai.mapper.ChatRecordMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【chat_record(AI Chat Record Table)】的数据库操作Service实现
* @createDate 2026-05-21 15:47:49
*/
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecord>
    implements ChatRecordService{

}




