package com.example.backend;

import com.example.backend.POJO.Flow;
import com.example.backend.POJO.Schedule;
import com.example.backend.Service.Arranger;
import com.example.backend.Service.EmployeeService;
import com.example.backend.Service.PreferenceService;
import com.example.backend.Service.Tool;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.FlowMapper;
import com.example.backend.mapper.ScheduleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class ArrangerTest {
    @Autowired
    Tool tool;
    @Autowired
    Arranger arranger;
    @Test
    void test(){
        String start="aoooeu 08:00:00 oahgoa wa";
        String regEx="\\d+\\d+:+\\d+\\d+:+\\d+\\d";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(start);
        if(m.find()){
            System.out.println(m.group().substring(0,5));
        }
        else System.out.println("not match");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2012,12,5,12,30);
        System.out.println(calendar.getTime());
        int a=12311;
    }
    @Test//排班算法测试
    void test2(){
        List<List<Arranger.TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        List<List<Tool.Flowtest>> flows=new ArrayList<>();
        flows.add(tool.getFlowTest());
        timeStaffNumList.addAll(arranger.arrangeWeekTest(1l,flows));
        arranger.outPut(timeStaffNumList,1,1);
    }
    @Autowired
    EmployeeService employeeService;
    @Test//employee相关测试
    void test3(){
        ResultVO<Object> resultVO;
//        resultVO=employeeService.addEmployee("xxx@qq.com","A","1",1l,1d,null,"08:00-10:00,10:00-12:00",null,null);
//        resultVO=employeeService.addEmployee("xxx1@qq.com","B","1",1l,1d,null,"08:00-10:00,10:00-12:00",null,null);
//        resultVO=employeeService.addEmployee("xxx2@qq.com","C","1",1l,1d,null,"09:00-12:00,13:00-15:00",null,null);
//        resultVO=employeeService.addEmployee("xxx3@qq.com","D","1",1l,1d,null,null,null,null);
//        resultVO=employeeService.addEmployee("xxx4@qq.com","E","1",1l,1d,null,"14:00-18:00,18:00-21:00",null,null);
//        resultVO=employeeService.addEmployee("xxx5@qq.com","F","1",1l,1d,null,"08:00-11:00,16:00-20:00",null,null);
//        resultVO=employeeService.addEmployee("xxx6@qq.com","G","1",1l,1d,null,"08:00-13:00,15:00-19:00",null,null);
//        resultVO=employeeService.addEmployee("xxx7@qq.com","H","1",1l,1d,null,"09:00-13:00,14:00-19:00",null,null);
//        resultVO=employeeService.addEmployee("xxx8@qq.com","I","1",1l,1d,null,null,null,null);
//        resultVO=employeeService.addEmployee("xxx9@qq.com","J","1",1l,1d,null,"13:00-18:00,10:00-12:00",null,null);
//        resultVO=employeeService.addEmployee("xxx10@qq.com","K","1",1l,1d,null,"12:00-15:00,16:00-19:00",null,null);
//        resultVO=employeeService.addEmployee("xxx11@qq.com","L","1",1l,1d,null,null,null,null);
        resultVO=employeeService.getEmployeeByShop(1l);
        System.out.println(resultVO.getMessage());
        System.out.println(resultVO.getData());
    }
    @Autowired
    PreferenceService preferenceService;
    @Test//preference相关测试
    void test4(){
        ResultVO<Object> resultVO= preferenceService.getPreference(1l);
        System.out.println(resultVO.getMessage());
        System.out.println(resultVO.getData());
    }
    @Autowired
    FlowMapper flowMapper;
    @Test//flow相关测试
    void test5(){
        //添加Flow
        //已添加成功
//        ArrayList<Flow.FlowUnit> flowUnits=new ArrayList<>();
//        Tool tool=new Tool();
//        List<Tool.Flowtest> flows=tool.getFlowTest();
//        int i=flowMapper.insert(tool.getFlow(2022,10,5,1l,1l,flows));
//        System.out.println(i);

        Flow flow=flowMapper.selectById(1l);
        ArrayList<Flow.FlowUnit> flowUnits=flow.getFlowUnits();
        System.out.println(flowUnits.get(1));
        Flow.FlowUnit flowUnit= flowUnits.get(1);
        System.out.println(flowUnit.getFlow());
        System.out.println(flowUnit.getBeginAt().toString());
        System.out.println(flowUnit.getEndAt());
    }
    @Test
    void test6(){
        List<List<Arranger.TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        timeStaffNumList.addAll(arranger.arrangeWeekTest(1,tool.getMoreFLowTest()));
        arranger.outPut(timeStaffNumList,1,1);
    }

    @Autowired
    ScheduleMapper scheduleMapper;
    @Test
    void test7(){
        Schedule schedule=scheduleMapper.selectById(1);
        System.out.println(schedule);
        Schedule.Week week=schedule.getWeeks().get(0);
        Schedule.WorkUnit workUnit=week.getData()[0][0];
        Long employee=workUnit.getEmployees().get(0);
        System.out.println(employee);
    }
}
