package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

import java.util.Date;

public interface RuleService {

    ResultVO<Object> addRule(Long shop, long manager, Double prepareTime, Double prepareWorkloadPerPerson, String preparePosition,
                             Double maxServiceNumber, String servicePosition, Integer numberOnDuty, Double closingTime,
                             Double closingWorkloadPerPersonU, Double closingWorkloadPerPersonV, String closingPosition,
                             Date startingDate, int lastingDays);

    ResultVO<Object> deleteRule(Long id);

    ResultVO<Object> updateRule();

    ResultVO<Object> getRule(Long id);

    ResultVO<Object> getRuleByShop(Long shopId);

}
