package com.example.backend.Controller;

import com.example.backend.Service.FlowService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/flow")
public class FlowController {

    @Autowired
    private FlowService flowService;

    @GetMapping("/shop")
    public ResultVO<Object> getFlowByShop(@RequestParam("shop") long shop,
                                          @RequestParam("start") Date date,
                                          @RequestParam("lasting") int lasting) {
        return flowService.getFlowByShop(shop, date, lasting);
    }

}
