package com.example.backend.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Schedule {

    private Long id;
    private Long shop;  // 该表所属的门店id
    private Long manager;  // 该表所属的管理员id
    private Date createAt;  // 排班表创建时间
    private Boolean isActive;  // 当前是否使用
    private Long useRule;  // 生成该表所使用的排班规则id

    private Date startAt;  // 该表所排班次开始日期
    private Date endAt;  // 该表所排班次结束日期

    private ArrayList<Week> weeks; // 排班内容，ArrayList每项为一周，每周为一个Week对象

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Week {
        private Date startAt;  // 本周开始日期
        private Date endAt;  // 本周结束日期
        private WorkUnit[][] data;  // 排班具体内容
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkUnit {
        // 班次最小单元，一个WorkUnit代表一个小时

        private Date beginTime;  // 该单元开始的日期时间

        private ArrayList<Long> employees;  // 该单元安排的员工id列表

    }

}
