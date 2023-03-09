package com.example.backend.Controller;

import com.example.backend.POJO.Flow;
import com.example.backend.Service.FlowService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.FlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/flow")
public class FlowController {

    @Autowired
    private FlowService flowService;

    @Autowired
    private FlowMapper flowMapper;

    @GetMapping("/shop")
    public ResultVO<Object> getFlowByShop(@RequestParam("shop") long shop,
                                          @RequestParam("start") Date date,
                                          @RequestParam("lasting") int lasting) {
        return flowService.getFlowByShop(shop, date, lasting);
    }


    // @GetMapping("/test")
    // 该接口仅用于测试，请勿打开上方注释
    public String test() {
        Date date = new Date();
        Random random = new Random();

        for (int i = 0; i < 50; i++) {

            ArrayList<Flow.FlowUnit> flowUnits = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 8);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            long timestamp = cal.getTime().getTime();

            for (int j = 0; j < 26; j++) {
                flowUnits.add(new Flow.FlowUnit(new Date(timestamp), new Date(timestamp + 1800000), random.nextDouble(0, 30)));
                timestamp += 1800000;
            }

            Flow flow = new Flow(null, 10L, date, flowUnits);
            flowMapper.insert(flow);
            date = new Date(date.getTime() + 86400000);
        }
        return "ok";
    }

}
