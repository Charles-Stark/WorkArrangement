package com.example.backend.VO;

import com.example.backend.POJO.Employee;
import com.example.backend.POJO.Preference;
import com.example.backend.POJO.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeVO {

    private Long id;
    private String uid;
    private String email;
    private String username;
    private String position;
    private Long shop;
    private Double salary;
    private Integer time;
    private String workingDay;
    private String workingHours;
    private Integer durationOfShift;
    private Integer durationOfWeek;

    public EmployeeVO(User user, Employee employee, Preference preference) {
        this.id = user.getId();
        this.uid = employee.getUid();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.position = employee.getPosition();
        this.shop = employee.getShop();
        this.salary = employee.getSalary();
        this.time = employee.getTime();

        this.workingDay = preference.getWorkingDay();
        this.workingHours = preference.getWorkingHours();
        this.durationOfShift = preference.getDurationOfShift();
        this.durationOfWeek = preference.getDurationOfWeek();
    }

}
