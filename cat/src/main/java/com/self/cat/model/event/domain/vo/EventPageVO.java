package com.self.cat.model.event.domain.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.self.cat.model.event.domain.Event;
import lombok.Data;

@Data
public class EventPageVO extends Page<Event>{

    /**
     * 待办数量
     */
    private Long uncompleted;

    /**
     * 分页数据
     */
    private Page<Event> page;
}