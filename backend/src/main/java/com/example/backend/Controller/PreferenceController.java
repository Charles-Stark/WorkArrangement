package com.example.backend.Controller;

import com.example.backend.Service.PreferenceService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preference")
public class PreferenceController {

    @Autowired
    private PreferenceService preferenceService;

    @GetMapping("/get/{id}")
    public ResultVO<Object> getPreference(@PathVariable Long id) {
        return preferenceService.getPreference(id);
    }

    @PostMapping("/update")
    public ResultVO<Object> updatePreference(@RequestParam("id") Long id,
                                             @RequestParam(value = "workingDay", required = false) String workingDay,
                                             @RequestParam(value = "workingHours", required = false) String workingHours,
                                             @RequestParam(value = "durationOfShift", required = false) Integer durationOfShift,
                                             @RequestParam(value = "durationOfWeek", required = false) Integer durationOfWeek) {
        return preferenceService.updatePreference(id, workingDay, workingHours, durationOfShift, durationOfWeek);
    }

}
