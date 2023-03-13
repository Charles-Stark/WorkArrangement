package com.example.backend.Service;

import com.example.backend.POJO.Flow;
import com.example.backend.VO.ResultVO;

import java.util.Date;
import java.util.List;

public interface FlowService {

    ResultVO<List<Flow>> getFlowByShop(long shopId, Date startDate, int lastingDays);

    void generateFlow(long shopId, Date startDate, int lastingDays);

}
