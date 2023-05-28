package com.example.backend.Controller;

import com.example.backend.Service.SurveyService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping("/uploadExcel")
    public ResultVO<Object> uploadExcel(@RequestParam("shopId") long shopId, @RequestParam("excelFile") MultipartFile excelFile) {
        return surveyService.uploadExcel(shopId, excelFile);
    }

    @GetMapping("/get/{id}")
    public ResultVO<Object> getSurvey(@PathVariable long id) {
        return surveyService.getSurveyByShop(id);
    }

}
