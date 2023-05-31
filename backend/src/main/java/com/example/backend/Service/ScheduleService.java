package com.example.backend.Service;

import com.example.backend.POJO.Schedule;
import com.example.backend.VO.ResultVO;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public interface ScheduleService {

    ResultVO<Object> getScheduleById(long id);

    ResultVO<List<Schedule>> getScheduleByShop(long id);

    ResultVO<Object> getSimplifiedScheduleByShop(long id);

    ResultVO<Object> getScheduleForEmployee(long employeeId);

    ResultVO<Object> getScheduleForShop(long shopId);

    long createSchedule(long shop, long manager, long rule, Date startAt, Date endAt, int lastingDays);

    ResultVO<Object> deleteSchedule(long id);

    ResultVO<LinkedList<Long>> getRecommend(long id, long begin, long now);

    boolean changeShift(long scheduleId, long previousEmployee, long currentEmployee, Date beginTime, boolean isOneDay);

}
