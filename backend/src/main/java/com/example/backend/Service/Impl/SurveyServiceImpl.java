package com.example.backend.Service.Impl;

import com.alibaba.excel.EasyExcel;
import com.example.backend.POJO.Survey;
import com.example.backend.Service.SurveyService;
import com.example.backend.Utils.ExcelListener;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.SurveyExcelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SurveyServiceImpl implements SurveyService {

    @Autowired
    private SurveyExcelMapper surveyExcelMapper;

    @Override
    public ResultVO<Object> uploadExcel(long shopId, MultipartFile excelFile) {
        try {
            ExcelListener excelListener = new ExcelListener();
            EasyExcel.read(excelFile.getInputStream(), Survey.class, excelListener).sheet().doRead();
            Survey survey = analyzeSurvey(shopId, excelListener.getDataList().get(0));
            surveyExcelMapper.insert(survey);

            return new ResultVO<>(0, "上传成功", survey);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultVO<>(-1, "上传失败", null);
        }
    }

    @Override
    public Survey analyzeSurvey(long shopId, Survey survey) {
        survey.setShop(shopId);
        survey.setOptimizedValue((survey.getQ5() * 1.846 - 2.974) / 0.229);

        return survey;
    }
}
