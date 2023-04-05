package com.example.backend.VO;

import com.example.backend.POJO.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleVO {

    private Long id;
    private Long shop;  // 该表所属的门店id
    private Long manager;  // 该表所属的管理员id
    private Date createAt;  // 排班表创建时间
    private Boolean isActive;  // 当前是否使用
    private Long useRule;  // 生成该表所使用的排班规则id

    private Date startAt;  // 该表所排班次开始日期
    private Date endAt;  // 该表所排班次结束日期

    public ScheduleVO(Schedule schedule) {
        id = schedule.getId();
        shop = schedule.getShop();
        manager = schedule.getManager();
        createAt = schedule.getCreateAt();
        isActive = schedule.getIsActive();
        useRule = schedule.getUseRule();
        startAt = schedule.getStartAt();
        endAt = schedule.getEndAt();
    }

}
