package com.example.backend.Service.Impl;

import com.example.backend.POJO.Employee;
import com.example.backend.POJO.Preference;
import com.example.backend.POJO.Shop;
import com.example.backend.POJO.User;
import com.example.backend.Service.EmployeeService;
import com.example.backend.Utils.HashUtil;
import com.example.backend.VO.EmployeeVO;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.EmployeeMapper;
import com.example.backend.mapper.PreferenceMapper;
import com.example.backend.mapper.ShopMapper;
import com.example.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PreferenceMapper preferenceMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Override
    public ResultVO<Object> addEmployee(String email, String username, String position, Long shop, Double salary, String workingDay, String workingHours, Integer durationOfShift, Integer durationOfWeek) {

        String uid = String.format("%03d", shop);

        User user = new User(null, email, HashUtil.getSHA256(email.split("@")[0] + "123456"), username, null, null, false);
        try {
            uid = uid.substring(uid.length() - 3) + String.format("%04d", getEmployeeNumberByShop(shop) + 1);

            userMapper.insert(user);
        } catch (Exception e) {
            return new ResultVO<>(-1, "添加员工失败", null);
        }

        Employee employee = new Employee(user.getId(), uid, position, shop, salary, 0);
        Preference preference = new Preference(user.getId(), workingDay, workingHours, durationOfShift, durationOfWeek);
        try {
            employeeMapper.insert(employee);
            preferenceMapper.insert(preference);
        } catch (Exception e) {
            userMapper.deleteById(user.getId());
            employeeMapper.deleteById(user.getId());
            preferenceMapper.deleteById(user.getId());
            return new ResultVO<>(-1, "添加员工失败", null);
        }

        return new ResultVO<>(0, "添加员工成功", new EmployeeVO(user, employee, preference));
    }

    @Override
    public ResultVO<Object> deleteEmployee(Long id) {

        // TODO update schedule

        try {
            preferenceMapper.deleteById(id);
            employeeMapper.deleteById(id);
            userMapper.deleteById(id);
        } catch (Exception e) {
            return new ResultVO<>(-1, "删除员工失败", null);
        }
        return new ResultVO<>(0, "删除员工成功", null);
    }

    @Override
    public ResultVO<Object> updateEmployee(Long id, String position, Long shop, Double salary) {

        // TODO update schedule

        Employee employee = new Employee(id, null, position, shop, salary, null);
        try {
            employeeMapper.updateById(employee);
        } catch (Exception e) {
            return new ResultVO<>(-1, "更新员工失败", null);
        }
        return new ResultVO<>(0, "更新员工成功", null);
    }

    @Override
    public ResultVO<Object> getEmployee(Long id) {
        try {
            return new ResultVO<>(0, "获取员工成功", new EmployeeVO(userMapper.selectById(id), employeeMapper.selectById(id), preferenceMapper.selectById(id)));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取员工失败", null);
        }
    }

    @Override
    public ResultVO<Object> getEmployeeByShop(Long shopId) {
        try {
            return new ResultVO<>(0, "获取员工成功", getEmployeeVOsByShop(shopId));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取员工失败", null);
        }
    }

    @Override
    public ResultVO<Object> getEmployeeByManager(Long managerId) {
        try {
            Map<String, Object> searchingMap = new HashMap<>();
            searchingMap.put("manager", managerId);
            List<Shop> shops = shopMapper.selectByMap(searchingMap);

            Map<Long, Object> resultMap = new HashMap<>();
            for (Shop shop : shops) {
                resultMap.put(shop.getId(), getEmployeeVOsByShop(shop.getId()));
            }
            return new ResultVO<>(0, "获取员工成功", resultMap);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取员工失败", null);
        }
    }

    private List<EmployeeVO> getEmployeeVOsByShop(Long shopId) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", shopId);
        List<Employee> employees = employeeMapper.selectByMap(searchingMap);

        List<EmployeeVO> employeeVOs = new ArrayList<>();
        for (Employee employee : employees) {
            employeeVOs.add(new EmployeeVO(userMapper.selectById(employee.getId()), employee, preferenceMapper.selectById(employee.getId())));
        }
        return employeeVOs;
    }

    private int getEmployeeNumberByShop(Long shop) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("shop", shop);
        return employeeMapper.selectByMap(searchingMap).size();
    }

}
