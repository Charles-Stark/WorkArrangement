package com.example.backend.Service.Impl;

import com.example.backend.POJO.Rule;
import com.example.backend.Service.RuleService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.RuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleMapper ruleMapper;

    @Override
    public ResultVO<Object> addRule(Long shop, Double prepareTime, Double prepareWorkloadPerPerson, String preparePosition, Double maxServiceNumber, String servicePosition, Integer numberOnDuty, Double closingTime, Double closingWorkloadPerPersonU, Double closingWorkloadPerPersonV, String closingPosition) {
        Rule rule = new Rule(null, shop, prepareTime, prepareWorkloadPerPerson, preparePosition, maxServiceNumber, servicePosition, numberOnDuty, closingTime, closingWorkloadPerPersonU, closingWorkloadPerPersonV, closingPosition);
        try {
            ruleMapper.insert(rule);
        } catch (Exception e) {
            return new ResultVO<>(-1, "添加规则失败", null);
        }
        return new ResultVO<>(0, "添加规则成功", ruleMapper.selectById(rule.getId()));
    }

    @Override
    public ResultVO<Object> deleteRule(Long id) {
        try {
            ruleMapper.deleteById(id);
            return new ResultVO<>(0, "删除排班规则成功", null);
        } catch (Exception e) {
            return new ResultVO<>(-1, "删除排班规则失败", null);
        }
    }

    @Override
    public ResultVO<Object> updateRule() {
        return null;
    }

    @Override
    public ResultVO<Object> getRule(Long id) {
        try {
            return new ResultVO<>(0, "获取排班规则成功", ruleMapper.selectById(id));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班规则失败", null);
        }
    }

    @Override
    public ResultVO<Object> getRuleByShop(Long shopId) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", shopId);
        try {
            return new ResultVO<>(0, "获取排班规则成功", ruleMapper.selectByMap(searchingMap));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取排班规则失败", null);
        }
    }
}
