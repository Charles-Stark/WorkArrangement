package com.example.backend.Service.Impl;

import com.example.backend.POJO.Flow;
import com.example.backend.Service.FlowService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.FlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlowServiceImpl implements FlowService {

    @Autowired
    private FlowMapper flowMapper;

    @Override
    public ResultVO<Object> getFlowByShop(long shopId, Date startDate, int lastingDays) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", shopId);
        List<Flow> flows = new ArrayList<>();
        try {
            long timestamp = startDate.getTime();
            while (lastingDays > 0) {
                searchingMap.put("date", new Date(timestamp));
                flows.add(flowMapper.selectByMap(searchingMap).get(0));
                lastingDays--;
                timestamp += 86400000;
            }
            return new ResultVO<>(0, "获取客流量成功", flows);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取客流量失败", null);
        }
    }
}
