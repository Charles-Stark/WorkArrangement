package com.example.backend.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.example.backend.POJO.Flow;
import com.example.backend.Service.FlowService;
import com.example.backend.Utils.OriginalFlowData;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.FlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FlowServiceImpl implements FlowService {

    private static final double FLOW_FLOATING_LOWER_BOUND = 0.8;
    private static final double FLOW_FLOATING_UPPER_BOUND = 1.2;

    @Autowired
    private FlowMapper flowMapper;

    private void sortFlowsByTimeOrder(List<Flow> flows) {
        Comparator<Flow> flowComparator = Comparator.comparing(Flow::getDate);
        flows.sort(flowComparator);
    }

    @Override
    public ResultVO<List<Flow>> getFlowByShop(long shopId, Date startDate, int lastingDays) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", shopId);
        List<Flow> flows = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long timestamp = startDate.getTime();
            while (lastingDays > 0) {
                String sDate = simpleDateFormat.format(new Date(timestamp));
                searchingMap.put("date", simpleDateFormat.parse(sDate));

                List<Flow> res = flowMapper.selectByMap(searchingMap);
                if (res.size() == 0) {
                    generateFlow(shopId, simpleDateFormat.parse(sDate), 30);
                    res = flowMapper.selectByMap(searchingMap);
                }
                flows.add(res.get(0));

                lastingDays--;
                timestamp += 86400000;  // 时间加上一天
            }
            sortFlowsByTimeOrder(flows);

            for (Flow flow : flows) {
                ArrayList<Flow.FlowUnit> flowUnits = flow.getFlowUnits();
                for (int i = 0; i < flowUnits.size(); i++) {
                    flowUnits.set(i, JSON.parseObject(JSON.toJSONString(flowUnits.get(i)), Flow.FlowUnit.class));
                }
            }

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
            cal.set(Calendar.HOUR_OF_DAY, 9);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long timestamp = cal.getTime().getTime();

            for (int j = 2; j < 26; j++) {
                flowUnits.add(new Flow.FlowUnit(new Date(timestamp),
                        new Date(timestamp + 1800000),
                        Double.parseDouble(String.format("%.2f", OriginalFlowData.data[j] * (random.nextDouble(FLOW_FLOATING_LOWER_BOUND, FLOW_FLOATING_UPPER_BOUND))))));
                timestamp += 1800000;  // 增加0.5小时
            }

            Flow flow = new Flow(null, shopId, startDate, flowUnits);
            flowMapper.insert(flow);
            startDate = new Date(startDate.getTime() + 86400000);  // 增加一天
        }
    }
}
