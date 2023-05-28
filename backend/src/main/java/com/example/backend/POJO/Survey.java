package com.example.backend.POJO;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Survey {

    @ExcelIgnore
    private Long id;
    @ExcelIgnore
    private long shop;

    @ExcelProperty(index = 1)
    private double satisfaction;
    @ExcelProperty(index = 2)
    private int nps;
    @ExcelProperty(index = 3)
    private int q1;
    @ExcelProperty(index = 4)
    private int q2;
    @ExcelProperty(index = 5)
    private int q3;
    @ExcelProperty(index = 6)
    private int q4;
    @ExcelProperty(index = 7)
    private int q5;
    @ExcelProperty(index = 8)
    private int q6;

    @ExcelIgnore
    private double optimizedValue;  // 系统推荐系数

}
