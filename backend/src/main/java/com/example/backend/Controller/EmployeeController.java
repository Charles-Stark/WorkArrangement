package com.example.backend.Controller;

import com.example.backend.Service.EmployeeService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PostMapping("/add")
    public ResultVO<Object> addEmployee(@RequestParam("email") String email,
                                        @RequestParam("username") String username,
                                        @RequestParam("position") String position,
                                        @RequestParam("shop") Long shop,
                                        @RequestParam("salary") Double salary,
                                        @RequestParam(value = "workingDay", required = false) String workingDay,
                                        @RequestParam(value = "workingHours", required = false) String workingHours,
                                        @RequestParam(value = "durationOfShift", required = false) Integer durationOfShift,
                                        @RequestParam(value = "durationOfWeek", required = false) Integer durationOfWeek) {
        return employeeService.addEmployee(email, username, position, shop, salary, workingDay, workingHours, durationOfShift, durationOfWeek);
    }

    @PostMapping("/delete")
    public ResultVO<Object> deleteEmployee(@RequestParam("id") Long id) {
        return employeeService.deleteEmployee(id);
    }

    @GetMapping("/get/{id}")
    public ResultVO<Object> getEmployee(@PathVariable Long id) {
        return employeeService.getEmployee(id);
    }

    @GetMapping("/get/byShop/{shopId}")
    public ResultVO<Object> getEmployeeByShop(@PathVariable Long shopId) {
        return employeeService.getEmployeeByShop(shopId);
    }

    @GetMapping("/get/byManager/{managerId}")
    public ResultVO<Object> getEmployeeByManager(@PathVariable Long managerId) {
        return employeeService.getEmployeeByManager(managerId);
    }

    @PostMapping("/update")
    public ResultVO<Object> updateEmployee(@RequestParam("id") Long id) {
        return employeeService.updateEmployee(id);
    }

}
