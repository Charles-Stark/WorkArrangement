package com.example.backend;

import com.example.backend.POJO.Flow;
import com.example.backend.POJO.Schedule;
import com.example.backend.Service.*;
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


//        resultVO=employeeService.getEmployeeByShop(1l);
//        System.out.println(resultVO.getMessage());
//        System.out.println(resultVO.getData());
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
    @Autowired
    FlowService flowService;
    @Test
    void test8(){
        List<List<Arranger.TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        Flow flow=tool.getFlow(2023,3,13,123l,1l,tool.getFlowTest());
        List<Flow> flows=new ArrayList<>();
        flows.add(flow);
        timeStaffNumList.addAll(arranger.arrangeWeek(1l,flows));
        //arranger.outPut(timeStaffNumList,1,1);
    }
}
