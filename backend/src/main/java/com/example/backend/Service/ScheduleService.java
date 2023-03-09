package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface ScheduleService {

    ResultVO<Object> getScheduleById(long id);

    ResultVO<Object> getScheduleByShop(long id);

}
