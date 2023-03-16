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
    @Autowired
    ArrangerNew arrangerNew;
    @Test
    void test2(){
        List<List<ArrangerNew.TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        Flow flow=tool.getFlow(2023,3,13,123l,6l,tool.getFlowTest());
        List<Flow> flows=new ArrayList<>();
        flows.add(flow);
        timeStaffNumList.addAll(arrangerNew.arrangeWeek(6l,flows,12l));
        //arrangerNew.outPut(timeStaffNumList,6,1,1);
    }
    @Autowired
    EmployeeService employeeService;
    @Test//employee相关测试
    void test3(){
        ResultVO<Object> resultVO;
        resultVO=employeeService.addEmployee("pNtod@qq.com","pibduMHNNN","1",6l,1d,null,null,7,36);
        resultVO=employeeService.addEmployee("isvCz@qq.com","OggFeJOILM","1",6l,1d,null,"14:00-18:00,14:00-16:00",7,39);
        resultVO=employeeService.addEmployee("JFKpL@qq.com","hkiazHHHOO","1",6l,1d,null,"10:00-14:00,09:00-11:00,08:00-12:00",null,null);
        resultVO=employeeService.addEmployee("gmDqx@qq.com","qNALGHMJML","1",6l,1d,null,"11:00-14:00,09:00-13:00,11:00-13:00",7,40);
        resultVO=employeeService.addEmployee("gCnPu@qq.com","ppIBvILGOK","1",6l,1d,"6,6,4",null,8,39);
        resultVO=employeeService.addEmployee("OinEG@qq.com","aBPlxINHLO","1",6l,1d,null,"10:00-12:00,16:00-20:00",6,39);
        resultVO=employeeService.addEmployee("Oklkw@qq.com","lsCPsMGIGP","1",6l,1d,"7,2,7","14:00-16:00,13:00-15:00,14:00-18:00",null,36);
        resultVO=employeeService.addEmployee("cMiAe@qq.com","lMKpqHLLGM","1",6l,1d,"3,6,7","15:00-20:00,17:00-20:00",7,36);
        resultVO=employeeService.addEmployee("oMkfF@qq.com","FeBENHPLNO","1",6l,1d,null,null,5,34);
        resultVO=employeeService.addEmployee("vrMrO@qq.com","agKcHMKOHJ","1",6l,1d,"4,6,2",null,4,39);
        resultVO=employeeService.addEmployee("cbweb@qq.com","NCtvIGOMHO","1",6l,1d,"6,3,5","09:00-13:00,16:00-18:00,12:00-14:00",5,35);
        resultVO=employeeService.addEmployee("BukkL@qq.com","LFrigKKOKP","1",6l,1d,"2,4,1","16:00-21:00,16:00-21:00",5,31);
        resultVO=employeeService.addEmployee("OrswG@qq.com","ALomEMIOOL","1",6l,1d,null,"16:00-19:00,08:00-10:00,16:00-20:00",4,34);
        resultVO=employeeService.addEmployee("KEdNG@qq.com","yzJDtINHHJ","1",6l,1d,"4,7,6",null,null,35);
        resultVO=employeeService.addEmployee("DOvFh@qq.com","MIkvHGGLGH","1",6l,1d,"4,5,7","11:00-14:00",null,30);
        resultVO=employeeService.addEmployee("BlaCk@qq.com","nkqimNPPHG","1",6l,1d,"5,6,7","13:00-18:00,16:00-20:00,15:00-20:00",5,31);
        resultVO=employeeService.addEmployee("npkdF@qq.com","bLsGzOOMHP","1",6l,1d,"3,1,4",null,4,40);
        resultVO=employeeService.addEmployee("ynvda@qq.com","DkeiPPMHNM","1",6l,1d,"3,7,2",null,8,39);
        resultVO=employeeService.addEmployee("JBNOG@qq.com","yFlnpNLHNH","1",6l,1d,null,null,6,32);
        resultVO=employeeService.addEmployee("LsOGv@qq.com","MHDxnNPONN","1",6l,1d,null,"08:00-11:00,16:00-19:00,17:00-19:00",8,38);


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
        Flow flow=tool.getFlow(2023,3,13,123l,6l,tool.getFlowTest());
        List<Flow> flows=new ArrayList<>();
        flows.add(flow);
        timeStaffNumList.addAll(arranger.arrangeWeek(6l,flows,12l));
        //arrangerNew.outPut(timeStaffNumList,6,1,1);
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
    @Test
    //arrangeWeek相关测试
    void test8(){
        List<List<Arranger.TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        Flow flow=tool.getFlow(2023,3,13,123l,6l,tool.getFlowTest());
        List<Flow> flows=new ArrayList<>();
        flows.add(flow);
        timeStaffNumList.addAll(arranger.arrangeWeek(6l,flows,1l));
        arranger.outPut(timeStaffNumList,6,1,1);
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
