package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface EmployeeService {

    ResultVO<Object> addEmployee(String email, String username, String uid, String position, Long shop);

}
