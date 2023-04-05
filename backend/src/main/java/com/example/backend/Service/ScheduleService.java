package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

import java.util.Date;

public interface ScheduleService {

    ResultVO<Object> getScheduleById(long id);

    ResultVO<Object> getScheduleByShop(long id);

    ResultVO<Object> getSimplifiedScheduleByShop(long id);

    ResultVO<Object> getScheduleForEmployee(long employeeId);

    ResultVO<Object> getScheduleForShop(long shopId);

    long createSchedule(long shop, long manager, long rule, Date startAt, Date endAt, int lastingDays);

}
