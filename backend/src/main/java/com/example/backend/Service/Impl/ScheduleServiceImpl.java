package com.example.backend.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.POJO.Employee;
import com.example.backend.POJO.EmployeeToSchedule;
import com.example.backend.POJO.Flow;
import com.example.backend.POJO.Schedule;
import com.example.backend.Service.*;
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

    @Autowired
    private EmployeeService employeeService;

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
    public ResultVO<List<Schedule>> getScheduleByShop(long id) {
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
            List<ScheduleVO> scheduleVOs = new ArrayList<>();
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
            Schedule schedule = scheduleMapper.selectById(scheduleId);
            Schedule resultSchedule = new Schedule(schedule.getId(), schedule.getShop(), schedule.getManager(), schedule.getCreateAt(), schedule.getIsActive(), schedule.getUseRule(), schedule.getStartAt(), schedule.getEndAt(), null);

            ArrayList<Schedule.Week> weeks = new ArrayList<>();

            for (int i = 0; i < schedule.getWeeks().size(); i++) {
                Schedule.Week week = JSON.parseObject(JSON.toJSONString(schedule.getWeeks().get(i)), Schedule.Week.class);
                Schedule.Week newWeek = new Schedule.Week(week.getStartAt(), week.getEndAt(), null);

                Schedule.WorkUnit[][] workUnits = week.getData();
                Schedule.WorkUnit[][] newWorkUnits = new Schedule.WorkUnit[workUnits.length][workUnits[0].length];

                for (int j = 0; j < workUnits.length; j++) {
                    for (int k = 0; k < workUnits[j].length; k++) {
                        if (workUnits[j][k] != null) {
                            if (workUnits[j][k].getEmployees().contains(employeeId)) {
                                ArrayList<Long> employees = new ArrayList<>();
                                employees.add(employeeId);
                                newWorkUnits[j][k] = new Schedule.WorkUnit(workUnits[j][k].getBeginTime(), employees);
                            }
                        }
                    }
                }
                newWeek.setData(newWorkUnits);
                weeks.add(newWeek);
            }
            resultSchedule.setWeeks(weeks);

            return new ResultVO<>(0, "获取排班表成功", resultSchedule);
        } catch (Exception e) {
            e.printStackTrace();
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
            /*
            List<List<Arranger.TimeStaffNum>> timeStaffNumList = new ArrayList<>();
            for (int i = 0; i < (flows.size() - 1) / 7 + 1; i++) {
                if (i * 7 + 7 > flows.size())
                    timeStaffNumList.addAll(arranger.arrangeWeek(shop, flows.subList(i * 7, flows.size()), rule));
                else timeStaffNumList.addAll(arranger.arrangeWeek(shop, flows.subList(i * 7, (i + 1) * 7), rule));
                System.out.println("完成本周排班，起始日期为" + flows.get(i).getDate());
            }
            */
            List<List<Arranger.TimeStaffNum>> timeStaffNumList = new ArrayList<>(arranger.arrangeMonth(shop, flows, rule));

            long scheduleId = arranger.outPut(timeStaffNumList, shop, rule, manager);

            Set<Long> employees = selectEmployeeInvolved(scheduleId);
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

    @Override
    public ResultVO<Object> deleteSchedule(long id) {
        try {
            scheduleMapper.deleteById(id);
        } catch (Exception e) {
            return new ResultVO<>(-1, "删除失败", null);
        }
        return new ResultVO<>(0, "删除成功", null);
    }

    @Override
    public ResultVO<Object> getRecommend(long id, int week, int day, int halfHour) {
        try {
            scheduleMapper.selectById(id);
            LinkedList<Long> employees = arranger.getSuitableEmployees(id, week, day, halfHour);
            LinkedList<Employee> employeeLinkedList = new LinkedList<>();
            for (long eid : employees) {
                employeeLinkedList.add((Employee) employeeService.getEmployee(eid).getData());
            }
            return new ResultVO<>(0, "获取成功", employeeLinkedList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO<>(-1, "获取失败", null);
        }
    }

    @Override
    public boolean changeShift(long scheduleId, long previousEmployee, long currentEmployee, Date beginTime, boolean isOneDay) {
        try {
            Schedule schedule = scheduleMapper.selectById(scheduleId);
            boolean afterPeriod = false;
            for (int i = 0; i < schedule.getWeeks().size(); i++) {
                Schedule.Week week = JSON.parseObject(JSON.toJSONString(schedule.getWeeks().get(i)), Schedule.Week.class);

                if (week.getEndAt().before(beginTime)) {
                    continue;
                }
                Schedule.WorkUnit[][] workUnits = week.getData();
                for (int j = 0; j < workUnits.length; j++) {
                    for (int k = 0; k < workUnits[j].length; k++) {
                        if (workUnits[j][k] != null && (workUnits[j][k].getBeginTime().compareTo(beginTime) == 0 || workUnits[j][k].getBeginTime().after(beginTime))) {
                            if (workUnits[j][k].getEmployees().contains(previousEmployee)) {
                                workUnits[j][k].getEmployees().remove(previousEmployee);
                                if (currentEmployee == 0 || !workUnits[j][k].getEmployees().contains(currentEmployee)) {
                                    workUnits[j][k].getEmployees().add(currentEmployee);
                                }
                            } else if (!isOneDay || !isSameDay(workUnits[j][k].getBeginTime(), beginTime)) {
                                afterPeriod = true;
                                break;
                            }
                        }
                    }
                    if (afterPeriod) {
                        break;
                    }
                }
                schedule.getWeeks().set(i, week);
                if (afterPeriod) {
                    break;
                }
            }
            scheduleMapper.updateById(schedule);

            if (currentEmployee == 0) {
                notifyAllEmployeesWhenOpenShift(scheduleId, schedule.getManager());
            } else {
                notifyAllEmployeesWhenScheduleChanged(scheduleId, schedule.getManager());
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isSameDay(Date a, Date b) {
        return a.getTime() / (24 * 60 * 60 * 1000L) == b.getTime() / (24 * 60 * 60 * 1000L);
    }

    private Set<Long> selectEmployeeInvolved(long scheduleId) {
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
        employees.removeIf(Objects::isNull);
        employees.removeIf(Objects -> Objects == 0);
        return employees;
    }

    private void notifyAllEmployeesWhenOpenShift(long scheduleId, long manager) {
        for (Long employee : selectEmployeeInvolved(scheduleId)) {
            notificationService.notifyWhenOpenShift(scheduleId, manager, employee);
        }
    }

    private void notifyAllEmployeesWhenScheduleChanged(long scheduleId, long manager) {
        for (Long employee : selectEmployeeInvolved(scheduleId)) {
            notificationService.notifyWhenScheduleChanged(scheduleId, manager, employee);
        }
    }

}
