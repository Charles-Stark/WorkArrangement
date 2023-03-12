package com.example.backend.Service.Impl;

import com.example.backend.POJO.Schedule;
import com.example.backend.Service.ScheduleService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public ResultVO<Object> getScheduleById(long id) {
        try {
            return new ResultVO<>(0, "获取排班表成功", scheduleMapper.selectById(id));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班表失败", null);
        }
    }

    @Override
    public ResultVO<Object> getScheduleByShop(long id) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", id);
        try {
            return new ResultVO<>(0, "获取排班表成功", scheduleMapper.selectByMap(searchingMap));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班表失败", null);
        }
    }

    @Override
    public long createSchedule(long shop, long manager, long rule, Date startAt, Date endAt) {
        Schedule schedule = new Schedule(null, shop, manager, null, true, rule, startAt, endAt, null);
        try {
            scheduleMapper.insert(schedule);

            // TODO 调用排班算法，排班并存入schedule

            return schedule.getId();
        } catch (Exception e) {
            return -1;
        }
    }
}
