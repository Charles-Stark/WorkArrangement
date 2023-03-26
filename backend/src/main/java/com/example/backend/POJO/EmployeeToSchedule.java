package com.example.backend.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeToSchedule {

    Long employeeId;  // 员工id
    Long scheduleId;  // 该员工所处的最新排班表id

}
