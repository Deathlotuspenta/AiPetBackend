package com.self.cat.model.event.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/event")
public class EventController {

    @GetMapping("/getMyEventList")
    public String getMyEventList() {
        return "获取我的活动列表";
    }
}
