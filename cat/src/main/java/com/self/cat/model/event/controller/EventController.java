package com.self.cat.model.event.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.self.cat.common.http.HttpResult;
import com.self.cat.common.utils.UserContext;
import com.self.cat.model.event.domain.AddEventDto;
import com.self.cat.model.event.domain.Event;
import com.self.cat.model.event.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    private static final String DEFAULT_ORDER = """
            ORDER BY
                is_completed ASC,
                CASE
                    WHEN event_time >= NOW() THEN 0
                    ELSE 1
                END ASC,
                event_time ASC
            """;

    @GetMapping("/getMyEventList")
    public HttpResult<Page<Event>> getMyEventList(
            String status,
            Integer pageNum,
            Integer pageSize,
            String keyword,
            Integer petId) {

        String userId = UserContext.get("id");

        Page<Event> page = new Page<>(
                pageNum,
                pageSize
        );

        LambdaQueryWrapper<Event> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Event::getUserId, userId);

        if (status != null && !status.isBlank()) {
            switch (status) {

                case "completed" -> {
                    queryWrapper.eq(Event::getIsCompleted, 1);
                    queryWrapper.orderByDesc(Event::getUpdateTime);
                }

                case "uncompleted" -> {
                    queryWrapper.eq(Event::getIsCompleted, 0);
                    queryWrapper.orderByAsc(Event::getEventTime);
                }

                case "expired" -> {
                    queryWrapper.eq(Event::getIsCompleted, 0);
                    queryWrapper.lt(Event::getEventTime, new Date());
                    queryWrapper.orderByDesc(Event::getEventTime);
                }

                default -> {
                    queryWrapper.last(DEFAULT_ORDER);
                }
            }
        } else {
            queryWrapper.last(DEFAULT_ORDER);
        }
        if (keyword != null && !keyword.isBlank()) {
            queryWrapper.and(wrapper ->
                    wrapper.like(Event::getEventName, keyword)
                            .or()
                            .like(Event::getEventContent, keyword)
                            .or()
                            .like(Event::getPetName, keyword)
            );
        }
        if (petId != null) {
            queryWrapper.eq(Event::getPetId, petId);
        }

        return HttpResult.success(
                eventService.page(page, queryWrapper)
        );
    }

    @PostMapping("/addMyEvent")
    public HttpResult<String> addMyEvent(@RequestBody AddEventDto addEventDto) {
        Event event = new Event();
        BeanUtils.copyProperties(addEventDto, event);
        event.setUserId(Integer.parseInt(UserContext.get("id")));
        event.setCreateTime(new Date());
        event.setUpdateTime(new Date());

        eventService.save(event);
        return HttpResult.success("添加成功");
    }

}
