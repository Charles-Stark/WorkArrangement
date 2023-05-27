package com.example.backend.Controller;

import com.example.backend.Service.ScheduleService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get/shop/newest/{id}")
    public ResultVO<Object> getNewestScheduleByShop(@PathVariable long id) {
        return scheduleService.getScheduleForShop(id);
    }

    @GetMapping("/get/shop/simplified/{id}")
    public ResultVO<Object> getSimplifiedScheduleByShop(@PathVariable long id) {
        return scheduleService.getSimplifiedScheduleByShop(id);
    }

    @GetMapping("/get/employee/{id}")
    public ResultVO<Object> getScheduleForEmployee(@PathVariable long id) {
        return scheduleService.getScheduleForEmployee(id);
    }

    @PostMapping("/delete/{id}")
    public ResultVO<Object> deleteSchedule(@PathVariable long id) {
        return scheduleService.deleteSchedule(id);
    }

    @RequestMapping("/get/recommend/{id}")
    public ResultVO<Object> getRecommend(@PathVariable long id,@RequestParam("week")int week,@RequestParam("day")int day,@RequestParam("halfHour")int halfHour){
        return scheduleService.getRecommend(id,week,day,halfHour);
    }
}
