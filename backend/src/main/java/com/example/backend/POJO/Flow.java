package com.example.backend.POJO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(autoResultMap = true)
public class Flow {

    private Long id;
    private Long shop;  // 该客流量数据所属门店的id
    private Date date;  // 该客流量数据的日期
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ArrayList<FlowUnit> flowUnits;  // 当天的客流量数据列表

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlowUnit {
        // 客流量数据最小单元，一个FlowUnit代表半个小时

        private Date beginAt;  // 该单元开始的时间
        private Date endAt;  // 该单元结束的时间
        private double flow;  // 该单元的客流量大小

    }
    public Flow(Flow flow){
        this.id=flow.id;
        this.date=flow.date;
        this.shop=flow.shop;
        this.flowUnits=flow.flowUnits;
    }
}
