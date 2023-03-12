package com.example.backend.Service.Impl;

import com.example.backend.POJO.Flow;
import com.example.backend.Service.FlowService;
import com.example.backend.Utils.OriginalFlowData;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.FlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FlowServiceImpl implements FlowService {

    @Autowired
    private FlowMapper flowMapper;

    private void sortFlowsByTimeOrder(List<Flow> flows) {
        Comparator<Flow> flowComparator = Comparator.comparing(Flow::getDate);
        flows.sort(flowComparator);
    }

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
                timestamp += 86400000;  // 时间加上一天
            }
            sortFlowsByTimeOrder(flows);
            return new ResultVO<>(0, "获取客流量成功", flows);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取客流量失败", null);
        }
    }

    @Override
    public void generateFlow(long shopId, Date startDate, int lastingDays) {
        Random random = new Random();
        OriginalFlowData originalFlowData = new OriginalFlowData();

        for (int i = 0; i < lastingDays; i++) {

            ArrayList<Flow.FlowUnit> flowUnits = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long timestamp = cal.getTime().getTime();

            for (int j = 0; j < 26; j++) {
                flowUnits.add(new Flow.FlowUnit(new Date(timestamp),
                        new Date(timestamp + 1800000),
                        Double.parseDouble(String.format("%.2f", OriginalFlowData.data[j] * (random.nextDouble(0.95, 1.05))))));
                timestamp += 1800000;  // 增加0.5小时
            }

            Flow flow = new Flow(null, shopId, startDate, flowUnits);
            flowMapper.insert(flow);
            startDate = new Date(startDate.getTime() + 86400000);  // 增加一天
        }
    }
}
