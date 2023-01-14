package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface EmployeeService {

    ResultVO<Object> addEmployee(String email, String username, String uid, String position, Long shop);

    ResultVO<Object> deleteEmployee(Long id);

    ResultVO<Object> updateEmployee(Long id);

    ResultVO<Object> getEmployee(Long id);

    ResultVO<Object> getEmployeeByShop(Long shopId);

    ResultVO<Object> getEmployeeByManager(Long managerId);

}
