package com.example.backend.Service;

import com.example.backend.POJO.Employee;
import com.example.backend.POJO.Flow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.List;

@Service
public class Tool {
    public Date timeTrans(Calendar calendar){
        return calendar.getTime();
    }
    public Calendar timeTrans(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
    public Flow getFlow(int year,int month,int day,long id,long shop,List<Date> begin,List<Date> end,List<Double> flows){
        Calendar c1=Calendar.getInstance();
        c1.set(year, month, day);
        ArrayList<Flow.FlowUnit> flowUnits=new ArrayList<>();
        for(int i=0;i<begin.size();i++) flowUnits.add(new Flow.FlowUnit(begin.get(i),end.get(i),flows.get(i)));
        Flow flow=new Flow(id,shop,c1.getTime(),flowUnits);
        return flow;
    }
    public Flow getFlow(int year,int month,int day,long id,long shop,List<Flowtest> flows){
        Calendar c1=Calendar.getInstance();
        c1.set(year, month, day);
        ArrayList<Flow.FlowUnit> flowUnits=new ArrayList<>();
        for(int i=0;i<flows.size();i++) {
            Date beginTime;
            Date endTime;
            c1.set(Calendar.HOUR_OF_DAY,Integer.parseInt(flows.get(i).begin.substring(0,2)));
            c1.set(Calendar.MINUTE,Integer.parseInt(flows.get(i).begin.substring(3,5)));
            beginTime=c1.getTime();
            int endHour=Integer.parseInt(flows.get(i).begin.substring(0,2));
            int endMinute=Integer.parseInt(flows.get(i).begin.substring(3,5))+30;
            if(endMinute==60) {
                endMinute=0;
                endHour++;
            }
            c1.set(Calendar.HOUR_OF_DAY,endHour);
            c1.set(Calendar.MINUTE,endMinute);
            endTime=c1.getTime();
            flowUnits.add(new Flow.FlowUnit(beginTime,endTime,flows.get(i).flow));
        }

        Flow flow=new Flow(id,shop,c1.getTime(),flowUnits);
        return flow;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Flowtest {
        public String begin;
        public double flow;
        public int dayOfWeek;
    }
    public ArrayList<Flowtest>getFlowTest(){
        ArrayList<Flowtest> flow=new ArrayList<>();
        flow.add(new Flowtest("08:00",0,1));
        flow.add(new Flowtest("08:30",0.1,1));
        flow.add(new Flowtest("09:00",1.3,1));
        flow.add(new Flowtest("09:30",5,1));
        flow.add(new Flowtest("10:00",11,1));
        flow.add(new Flowtest("10:30",13,1));
        flow.add(new Flowtest("11:00",13,1));
        flow.add(new Flowtest("11:30",13,1));
        flow.add(new Flowtest("12:00",12,1));
        flow.add(new Flowtest("12:30",13,1));
        flow.add(new Flowtest("13:00",12,1));
        flow.add(new Flowtest("13:30",14,1));
        flow.add(new Flowtest("14:00",15,1));
        flow.add(new Flowtest("14:30",14,1));
        flow.add(new Flowtest("15:00",15,1));
        flow.add(new Flowtest("15:30",11,1));
        flow.add(new Flowtest("16:00",11,1));
        flow.add(new Flowtest("16:30",8,1));
        flow.add(new Flowtest("17:00",8,1));
        flow.add(new Flowtest("17:30",7,1));
        flow.add(new Flowtest("18:00",5,1));
        flow.add(new Flowtest("18:30",5,1));
        flow.add(new Flowtest("19:00",2,1));
        flow.add(new Flowtest("19:30",2,1));
        flow.add(new Flowtest("20:00",1,1));
        flow.add(new Flowtest("20:30",1,1));
        return flow;
    }
    public List<List<Flowtest>> getMoreFLowTest(){
        List<Flowtest> flow = getFlowTest();
        List<List<Flowtest>> flows=new ArrayList<>();
        flows.add(flow);
        for(int i=0;i<6;i++){
            ArrayList<Flowtest> f=new ArrayList<>();
            for(Flowtest flowtest:flow){
                double flownew=flowtest.flow*(1+(Math.random()*10-5)/100);
                f.add(new Flowtest(flowtest.begin,flownew,flowtest.dayOfWeek+i+1));
            }
            flows.add(f);
        }
        return flows;
    }
    public List<Employee> addEmployee(){
        List<Employee> employeeList=new ArrayList<Employee>();
        employeeList.add(new Employee(1l,"A","a",1l,1d,1));
        employeeList.add(new Employee(2l,"B","a",1l,1d,1));
        employeeList.add(new Employee(3l,"C","a",1l,1d,1));
        employeeList.add(new Employee(4l,"D","a",1l,1d,1));
        employeeList.add(new Employee(5l,"E","a",1l,1d,1));
        employeeList.add(new Employee(6l,"F","a",1l,1d,1));
        employeeList.add(new Employee(7l,"G","a",1l,1d,1));
        employeeList.add(new Employee(8l,"H","a",1l,1d,1));
        return employeeList;
    }
}
