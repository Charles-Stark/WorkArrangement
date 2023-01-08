package com.example.backend.Service.Impl;

import com.example.backend.POJO.Employee;
import com.example.backend.POJO.User;
import com.example.backend.Service.EmployeeService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.EmployeeMapper;
import com.example.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ResultVO<Object> addEmployee(String email, String username, String uid, String position, Long shop) {

        // TODO: Generate original password
        User user = new User(null, email, "password", username, null, null, false);
        try {
            userMapper.insert(user);
        } catch (Exception e) {
            return new ResultVO<>(-1, "添加员工失败", null);
        }

        // TODO: Generate UID
        Employee employee = new Employee(user.getId(), "uid", position, shop, 0D, 0);
        try {
            employeeMapper.insert(employee);
        } catch (Exception e) {
            // TODO: Delete user if employee not inserted
            return new ResultVO<>(-1, "添加员工失败", null);
        }

        // TODO: VO for User + Employee
        return new ResultVO<>(0, "添加员工成功", "user + employee");
    }
}
