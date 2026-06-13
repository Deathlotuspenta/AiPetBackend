package com.self.cat.model.event.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.self.cat.common.enums.SourceName;
import com.self.cat.common.http.HttpResult;
import com.self.cat.common.service.PermissionService;
import com.self.cat.common.utils.UserContext;
import com.self.cat.model.event.domain.dto.AddEventDto;
import com.self.cat.model.event.domain.Event;
import com.self.cat.model.event.domain.vo.EventPageVO;
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
    private final PermissionService permissionService;

    public EventController(EventService eventService, PermissionService permissionService) {
        this.eventService = eventService;
        this.permissionService = permissionService;
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

    @PutMapping("/updateMyEvent/{id}")
    public HttpResult<String> updateMyEvent(
            @PathVariable("id") Long id,
            @RequestBody Event event
    ){
        Long userId = Long.valueOf(UserContext.get("id"));
        boolean b = permissionService.hasPermission(userId, SourceName.EVENT, id);
        if (!b) {
            return HttpResult.error(403, "无权限");
        }
        event.setId(Math.toIntExact(id));
        return HttpResult.success(eventService.updateById(event) ? "更新成功" : "更新失败");
    }

    @DeleteMapping("/deleteMyEvent/{id}")
    public HttpResult<String> deleteMyEvent(
            @PathVariable("id") Long id
    ){
        Long userId = Long.valueOf(UserContext.get("id"));
        boolean b = permissionService.hasPermission(userId, SourceName.EVENT, id);
        if (!b) {
            return HttpResult.error(403, "无权限");
        }
        return HttpResult.success(eventService.removeById(id) ? "删除成功" : "删除失败");
    }

    @GetMapping("/getMyEventList")
    public HttpResult<EventPageVO> getMyEventList(
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

        // 查找待完成的
        LambdaQueryWrapper<Event> queryUncompletedQuery = new LambdaQueryWrapper<>();
        queryUncompletedQuery.eq(Event::getUserId, userId)
                .eq(Event::getIsCompleted, 0);
        if (keyword != null && !keyword.isBlank()){
            queryUncompletedQuery.and(wrapper ->
                    wrapper.like(Event::getEventName, keyword)
                            .or()
                            .like(Event::getEventContent, keyword)
                            .or()
                            .like(Event::getPetName, keyword)
            );
        }
        if (petId != null){
            queryUncompletedQuery.eq(Event::getPetId, petId);
        }

        long uncompletedCount = eventService.count(queryUncompletedQuery);
        Page<Event> dataPage = eventService.page(page, queryWrapper);
        EventPageVO eventPageVO = new EventPageVO();

        BeanUtils.copyProperties(dataPage, eventPageVO);

        eventPageVO.setUncompleted(uncompletedCount);

        return HttpResult.success(
            eventPageVO
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
