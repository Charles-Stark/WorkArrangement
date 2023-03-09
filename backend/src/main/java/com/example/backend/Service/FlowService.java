package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

import java.util.Date;

public interface FlowService {

    ResultVO<Object> getFlowByShop(long shopId, Date startDate, int lastingDays);

    void generateFlow(long shopId, Date startDate, int lastingDays);

}
