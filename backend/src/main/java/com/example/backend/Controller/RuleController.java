package com.example.backend.Controller;

import com.example.backend.Service.RuleService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping("/add")
    public ResultVO<Object> addRule(@RequestParam("shop") long shop,
                                    @RequestParam("manager") long manager,
                                    @RequestParam(value = "prepareTime", required = false) double prepareTime,
                                    @RequestParam(value = "prepareWorkloadPerPerson", required = false) double prepareWorkloadPerPerson,
                                    @RequestParam(value = "preparePosition", required = false) String preparePosition,
                                    @RequestParam(value = "maxServiceNumber", required = false) double maxServiceNumber,
                                    @RequestParam(value = "servicePosition", required = false) String servicePosition,
                                    @RequestParam(value = "numberOnDuty", required = false) int numberOnDuty,
                                    @RequestParam(value = "closingTime", required = false) double closingTime,
                                    @RequestParam(value = "closingWorkloadPerPersonU", required = false) double closingWorkloadPerPersonU,
                                    @RequestParam(value = "closingWorkloadPerPersonV", required = false) double closingWorkloadPerPersonV,
                                    @RequestParam(value = "closingPosition", required = false) String closingPosition,
                                    @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate,
                                    @RequestParam("lastingDays") int lastingDays) {
        return ruleService.addRule(shop, manager, prepareTime, prepareWorkloadPerPerson, preparePosition, maxServiceNumber, servicePosition, numberOnDuty, closingTime, closingWorkloadPerPersonU, closingWorkloadPerPersonV, closingPosition, startDate, lastingDays);
    }

    @PostMapping("/delete")
    public ResultVO<Object> deleteRule(@RequestParam("id") Long id) {
        return ruleService.deleteRule(id);
    }

    @GetMapping("/get/{id}")
    public ResultVO<Object> getRule(@PathVariable Long id) {
        return ruleService.getRule(id);
    }

    @GetMapping("/getAll/{shopId}")
    public ResultVO<Object> getRuleByShop(@PathVariable Long shopId) {
        return ruleService.getRuleByShop(shopId);
    }

}
