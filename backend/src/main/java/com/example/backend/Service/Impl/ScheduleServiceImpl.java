package com.example.backend.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.POJO.EmployeeToSchedule;
import com.example.backend.POJO.Flow;
import com.example.backend.POJO.Schedule;
import com.example.backend.Service.Arranger;
import com.example.backend.Service.FlowService;
import com.example.backend.Service.NotificationService;
import com.example.backend.Service.ScheduleService;
import com.example.backend.VO.ResultVO;
import com.example.backend.VO.ScheduleVO;
import com.example.backend.mapper.EmployeeToScheduleMapper;
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
    private EmployeeToScheduleMapper employeeToScheduleMapper;

    @Autowired
    @Lazy
    private Arranger arranger;

    @Autowired
    private FlowService flowService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ResultVO<Object> getScheduleById(long id) {
        try {
            return new ResultVO<>(0, "获取排班表成功", scheduleMapper.selectById(id));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班表失败", null);
        }
    }

    private void sortSchedulesByTimeOrder(List<Schedule> schedules) {
        Comparator<Schedule> scheduleComparator = Comparator.comparing(Schedule::getCreateAt);
        schedules.sort(scheduleComparator);
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
    public ResultVO<Object> getSimplifiedScheduleByShop(long id) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", id);
        try {
            List<Schedule> schedules = scheduleMapper.selectByMap(searchingMap);
            List<ScheduleVO>scheduleVOs = new ArrayList<>();
            for (Schedule schedule : schedules) {
                scheduleVOs.add(new ScheduleVO(schedule));
            }
            return new ResultVO<>(0, "获取排班表成功", scheduleVOs);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班表失败", null);
        }
    }

    @Override
    public ResultVO<Object> getScheduleForEmployee(long employeeId) {
        try {
            long scheduleId = employeeToScheduleMapper.selectById(employeeId).getScheduleId();
            return new ResultVO<>(0, "获取排班表成功", scheduleMapper.selectById(scheduleId));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班表失败", null);
        }
    }

    @Override
    public ResultVO<Object> getScheduleForShop(long shopId) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", shopId);
        try {
            List<Schedule> schedules = scheduleMapper.selectByMap(searchingMap);
            sortSchedulesByTimeOrder(schedules);
            return new ResultVO<>(0, "获取排班表成功", schedules.get(schedules.size() - 1));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班表失败", null);
        }
    }

    @Override
    public long createSchedule(long shop, long manager, long rule, Date startAt, Date endAt, int lastingDays) {
        try {
            // 调用排班算法，排班并存入schedule
            List<Flow> flows = flowService.getFlowByShop(shop, startAt, lastingDays).getData();
            List<List<Arranger.TimeStaffNum>> timeStaffNumList = new ArrayList<>();
            for (int i = 0; i < (flows.size() - 1) / 7 + 1; i++) {
                if (i * 7 + 7 > flows.size())
                    timeStaffNumList.addAll(arranger.arrangeWeek(shop, flows.subList(i * 7, flows.size()), rule));
                else timeStaffNumList.addAll(arranger.arrangeWeek(shop, flows.subList(i * 7, (i + 1) * 7), rule));
                System.out.println("完成本周排班，起始日期为" + flows.get(i).getDate());
            }
            long scheduleId = arranger.outPut(timeStaffNumList, shop, rule, manager);

            Schedule schedule = scheduleMapper.selectById(scheduleId);
            Set<Long> employees = new HashSet<>();
            for (int i = 0; i < schedule.getWeeks().size(); i++) {
                Schedule.Week week = JSON.parseObject(JSON.toJSONString(schedule.getWeeks().get(i)), Schedule.Week.class);
                Schedule.WorkUnit[][] workUnits = week.getData();
                for (int j = 0; j < workUnits.length; j++) {
                    for (int k = 0; k < workUnits[j].length; k++) {
                        if (workUnits[j][k] != null) {
                            employees.addAll(workUnits[j][k].getEmployees());
                        }
                    }
                }
            }
            for (Long employee : employees) {
                // 产生了新排班表，创建通知
                notificationService.notifyWhenNewScheduleCreated(scheduleId, manager, employee);
                // 关联员工id和表id
                EmployeeToSchedule employeeToSchedule = new EmployeeToSchedule(employee, scheduleId);
                if (employeeToScheduleMapper.selectCount(new QueryWrapper<EmployeeToSchedule>().eq("employeeId", employee)) > 0) {
                    employeeToScheduleMapper.updateById(employeeToSchedule);
                } else {
                    employeeToScheduleMapper.insert(employeeToSchedule);
                }
            }

            return scheduleId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
