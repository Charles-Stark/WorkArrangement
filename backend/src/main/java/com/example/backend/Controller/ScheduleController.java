package com.example.backend.Controller;

import com.example.backend.Service.ScheduleService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    @Autowired
    ScheduleService scheduleService;

    @GetMapping("/get/{id}")
    public ResultVO<Object> getScheduleById(@PathVariable long id) {
        return scheduleService.getScheduleById(id);
    }

    @GetMapping("/get/shop/{id}")
    public ResultVO<Object> getScheduleByShop(@PathVariable long id) {
        return scheduleService.getScheduleByShop(id);
    }

    @GetMapping("/get/employee/{id}")
    public ResultVO<Object> getScheduleForEmployee(@PathVariable long id) {
        return scheduleService.getScheduleForEmployee(id);
    }

}
