package com.example.backend.Service;

import com.example.backend.POJO.Survey;
import com.example.backend.VO.ResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface SurveyService {

    ResultVO<Object> uploadExcel(long shopId, MultipartFile excelFile);

    Survey analyzeSurvey(long shopId, Survey surveyList);

}
