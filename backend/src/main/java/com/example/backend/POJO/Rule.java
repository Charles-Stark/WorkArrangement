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

}
