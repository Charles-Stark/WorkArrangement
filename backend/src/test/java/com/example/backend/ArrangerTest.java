package com.example.backend;

import com.example.backend.POJO.Flow;
import com.example.backend.POJO.Rule;
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
    @Autowired
    EmployeeService employeeService;
    @Test//employee相关测试
    void test3(){
        ResultVO<Object> resultVO;
        resultVO=employeeService.addEmployee("wALoC@qq.com","amoxnNIKON","1",7l,1d,null,"09:00-13:00,08:00-10:00",5,31);
        resultVO=employeeService.addEmployee("DdEoq@qq.com","spHkvKJPPM","1",7l,1d,"6,6,7",null,8,30);
        resultVO=employeeService.addEmployee("xzJfC@qq.com","LygkbHOLPP","1",7l,1d,null,"17:00-22:00,15:00-19:00",null,33);
        resultVO=employeeService.addEmployee("pywur@qq.com","bFyPDONKJO","1",7l,1d,"3,1,2","17:00-21:00,10:00-13:00,10:00-14:00",5,35);


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
    @Autowired
    FlowService flowService;
    @Test//flow相关测试
    void test5(){
        Flow flow=flowMapper.selectById(1l);
        ArrayList<Flow.FlowUnit> flowUnits=flow.getFlowUnits();
        System.out.println(flowUnits.get(1));
        Flow.FlowUnit flowUnit= flowUnits.get(1);
        System.out.println(flowUnit.getFlow());
        System.out.println(flowUnit.getBeginAt().toString());
        System.out.println(flowUnit.getEndAt());
    }
    @Autowired
    ScheduleMapper scheduleMapper;
    @Test
    //schedule相关测试
    void test7(){
        Schedule schedule=scheduleMapper.selectById(1);
        System.out.println(schedule);
        Schedule.Week week=schedule.getWeeks().get(0);
        Schedule.WorkUnit workUnit=week.getData()[0][0];
        Long employee=workUnit.getEmployees().get(0);
        System.out.println(employee);
    }
    @Autowired
    RuleService ruleService;
    @Test
    //rule相关测试
    void test9(){
        //ruleService.addRule(1l,1l,1d,1d,"abc",8d,"ab",1,1d,1d,1d,"a",new Date(),7);
        List<Rule> rules=(ArrayList<Rule>)ruleService.getRuleByShop(1l).getData();
        Rule rule=rules.get(0);
        System.out.println(rule);
    }

}
