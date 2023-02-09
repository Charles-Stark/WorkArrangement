package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface EmployeeService {

    ResultVO<Object> addEmployee(String email, String username, String position, Long shop, Double salary, String workingDay, String workingHours, Integer durationOfShift, Integer durationOfWeek);

    ResultVO<Object> deleteEmployee(Long id);

    ResultVO<Object> updateEmployee(Long id, String position, Long shop, Double salary, Integer time);

    ResultVO<Object> getEmployee(Long id);

    ResultVO<Object> getEmployeeByShop(Long shopId);

    ResultVO<Object> getEmployeeByManager(Long managerId);

}
