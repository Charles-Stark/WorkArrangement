package com.example.backend.Controller;

import com.example.backend.POJO.Schedule;
import com.example.backend.Service.ScheduleService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    public ResultVO<List<Schedule>> getScheduleByShop(@PathVariable long id) {
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

    @PostMapping("/get/recommend/{id}")
    public ResultVO<LinkedList<Long>> getRecommend(@PathVariable long id, @RequestParam("begin") long begin, @RequestParam("now") long now) {
        return scheduleService.getRecommend(id, begin, now);
    }

    @PostMapping("/changeShift")
    public ResultVO<Object> changeShift(@RequestParam("schedule") long schedule, @RequestParam("previousEmployee") long previousEmployee, @RequestParam("currentEmployee") long currentEmployee, @RequestParam("beginTime") long beginTime) {
        if (scheduleService.changeShift(schedule, previousEmployee, currentEmployee, new Date(beginTime), false)) {
            return new ResultVO<>(0, "换班成功", null);
        } else {
            return new ResultVO<>(-1, "换班失败", null);
        }
    }

}
