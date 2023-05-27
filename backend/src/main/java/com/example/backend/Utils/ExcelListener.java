package com.example.backend.Utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.example.backend.POJO.Survey;

import java.util.ArrayList;
import java.util.List;

public class ExcelListener implements ReadListener<Survey> {

    private final List<Survey> dataList = new ArrayList<>();

    @Override
    public void invoke(Survey survey, AnalysisContext analysisContext) {
        dataList.add(survey);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<Survey> getDataList() {
        return dataList;
    }

}
