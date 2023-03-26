package com.example.backend.POJO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("EmployeeToSchedule")
public class EmployeeToSchedule {

    @TableId(type = IdType.NONE)
    Long employeeId;  // 员工id
    Long scheduleId;  // 该员工所处的最新排班表id

}
