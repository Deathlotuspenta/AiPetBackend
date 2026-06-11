package com.self.cat.model.event.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.self.cat.model.event.domain.Event;
import com.self.cat.model.event.service.EventService;
import com.self.cat.model.event.mapper.EventMapper;
import org.springframework.stereotype.Service;

/**
* @author EDY
* @description 针对表【event】的数据库操作Service实现
* @createDate 2026-06-11 11:13:01
*/
@Service
public class EventServiceImpl extends ServiceImpl<EventMapper, Event>
    implements EventService{

}




