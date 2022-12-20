package com.example.backend.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Preference {

    private Long id;
    private String workingDay;
    private String workingHours;
    private Integer durationOfShift;
    private Integer durationOfWeek;

}
