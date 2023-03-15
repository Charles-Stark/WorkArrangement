package com.example.backend.Service.Impl;

import com.example.backend.POJO.Flow;
import com.example.backend.Service.Arranger;
import com.example.backend.Service.FlowService;
import com.example.backend.Service.ScheduleService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.ScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Autowired
    @Lazy
    private Arranger arranger;

    @Autowired
    private FlowService flowService;

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
    public long createSchedule(long shop, long manager, long rule, Date startAt, Date endAt, int lastingDays) {
        try {
            // 调用排班算法，排班并存入schedule
            List<Flow> flows = flowService.getFlowByShop(shop, startAt, lastingDays).getData().subList(0, 1);
            List<List<Arranger.TimeStaffNum>> timeStaffNumList = new ArrayList<>();
            timeStaffNumList.addAll(arranger.arrangeWeek(shop, flows));
            return arranger.outPut(timeStaffNumList, shop, rule, manager);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
