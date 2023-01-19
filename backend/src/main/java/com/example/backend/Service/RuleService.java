package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface RuleService {

    ResultVO<Object> addRule(Long shop, Double prepareTime, Double prepareWorkloadPerPerson, String preparePosition,
                             Double maxServiceNumber, String servicePosition, Integer numberOnDuty, Double closingTime,
                             Double closingWorkloadPerPersonU, Double closingWorkloadPerPersonV, String closingPosition);

    ResultVO<Object> deleteRule(Long id);

    ResultVO<Object> updateRule();

    ResultVO<Object> getRule(Long id);

    ResultVO<Object> getRuleByShop(Long shopId);

}
