package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

import java.sql.Timestamp;
import java.util.Date;

public interface ScheduleService {

    ResultVO<Object> getScheduleById(long id);

    ResultVO<Object> getScheduleByShop(long id);

    ResultVO<Object> getSimplifiedScheduleByShop(long id);

    ResultVO<Object> getScheduleForEmployee(long employeeId);

    ResultVO<Object> getScheduleForShop(long shopId);

    long createSchedule(long shop, long manager, long rule, Date startAt, Date endAt, int lastingDays);

    ResultVO<Object> deleteSchedule(long id);

    ResultVO<Object> getRecommend(long id, int week, int day, int halfHour);

    boolean changeShift(long schedule, long previousEmployee, long currentEmployee, Date beginTime);

}
