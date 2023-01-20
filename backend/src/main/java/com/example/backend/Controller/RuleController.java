package com.example.backend.Controller;

import com.example.backend.Service.RuleService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rule")
public class RuleController {

    @Autowired
    private RuleService ruleService;

    @PostMapping("/add")
    public ResultVO<Object> addRule(@RequestParam("shop") Long shop,
                                    @RequestParam(value = "prepareTime", required = false) Double prepareTime,
                                    @RequestParam(value = "prepareWorkloadPerPerson", required = false) Double prepareWorkloadPerPerson,
                                    @RequestParam(value = "preparePosition", required = false) String preparePosition,
                                    @RequestParam(value = "maxServiceNumber", required = false) Double maxServiceNumber,
                                    @RequestParam(value = "servicePosition", required = false) String servicePosition,
                                    @RequestParam(value = "numberOnDuty", required = false) Integer numberOnDuty,
                                    @RequestParam(value = "closingTime", required = false) Double closingTime,
                                    @RequestParam(value = "closingWorkloadPerPersonU", required = false) Double closingWorkloadPerPersonU,
                                    @RequestParam(value = "closingWorkloadPerPersonV", required = false) Double closingWorkloadPerPersonV,
                                    @RequestParam(value = "closingPosition", required = false) String closingPosition) {
        return ruleService.addRule(shop, prepareTime, prepareWorkloadPerPerson, preparePosition, maxServiceNumber, servicePosition, numberOnDuty, closingTime, closingWorkloadPerPersonU, closingWorkloadPerPersonV, closingPosition);
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
