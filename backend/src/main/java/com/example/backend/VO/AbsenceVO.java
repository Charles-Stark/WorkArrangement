package com.example.backend.VO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenceVO {

    private Long id;
    private EmployeeVO employeeVO;
    private Long managerId;  // 管理员id
    private Long shopId;  // 店铺id
    private String reason;  // 请假原因
    @JsonIgnore
    private byte[] attachmentPhoto;  // 图片附件
    @JsonIgnore
    private String photoType;  // 图片附件类型
    private Boolean isApproved;  // 请假是否通过
    private Date absenceDate;  // 请假日期
    private Date createAt;  // 创建时间

}
