package com.example.backend.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rule {

    private Long id;
    private Long shop;
    private Double prepareTime;
    private Double prepareWorkloadPerPerson;
    private String preparePosition;
    private Double maxServiceNumber;
    private String servicePosition;
    private Integer numberOnDuty;
    private Double closingTime;
    private Double closingWorkloadPerPersonU;
    private Double closingWorkloadPerPersonV;
    private String closingPosition;
    private Boolean balance;  // 均衡排班
    private Integer minimumWorkingHourPerMonth;  // 最小月工作时长
    private Integer maximumContinuousWorkingDays;  // 最大连续工作天数

}
