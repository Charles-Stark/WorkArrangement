package com.example.backend.Service;

import java.sql.Time;
import java.util.*;

import com.example.backend.POJO.*;
import com.example.backend.VO.EmployeeVO;
import com.example.backend.mapper.ScheduleMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.*;
@Service
public class Arranger {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private RuleService ruleService;
    @Autowired
    private PreferenceService preferenceService;
    @Autowired
    private ScheduleMapper scheduleMapper;
    private final int unitNum=4;//一个为半小时
    private double maxServiceNumber=3.8;
    private int numberOnDuty=1;
    private double prepareTime=1;
    private double closingTime=1;
    private double monthMin=120;
    private int maxWorkingDay=5;
    private List<Prefer> preference;
    private List<Staff> staffList;
    private Map<Staff,Float> matchingDegree;
    private String prepare,closing;
    private boolean balanced=false;
    public void setRule(Rule rule){
        prepare=null;
        closing=null;
        if (rule==null) return;
        if(rule.getPrepareTime()!=null) prepareTime = rule.getPrepareTime();
        if(rule.getClosingTime()!=null) closingTime = rule.getClosingTime();
        if(rule.getNumberOnDuty()!=null) this.numberOnDuty=rule.getNumberOnDuty();
        if(rule.getMaxServiceNumber()!=null) this.maxServiceNumber=rule.getMaxServiceNumber();
        if(rule.getClosingPosition()!=null) closing=rule.getClosingPosition();
        if(rule.getPreparePosition()!=null) prepare=rule.getPreparePosition();
    }
    private Staff findStaff(Long id){
        if(staffList.get(0).getClass()== Staff.class)
            for(Staff s:staffList)
                if(id.equals(s.id))
                    return s;
        return null;
    }
    public boolean hasSelected(Staff staff, List<TimeStaffNum> timeStaffNums,int index){
        if(timeStaffNums.get(index).contains(staff)) return true;
        if(index>0) return timeStaffNums.get(index-1).contains(staff);
        return false;
    }
    public boolean isFull(List<TimeStaffNum> timeStaffNumList){
        for(TimeStaffNum timeStaffNum:timeStaffNumList)
            if(!timeStaffNum.isFull()) return false;
        return true;
    }
    public class TimeStaffNum {     //记录时间段中员工数量
        private int minStaffNum;   // 最少员工数
        private Date startTime;
        private int currentStaffNum;     //目前员工人数
        private ArrayList<Staff> staff;
        private ArrayList<TimeStaffNum.WorkUnit> workUnits;
        public TimeStaffNum(ArrayList<Flow.FlowUnit> flowUnits, int index, int minStaffNum, int unitNum) {
            startTime=flowUnits.get(index).getBeginAt();
            this.minStaffNum = minStaffNum;
            if(minStaffNum==0) this.minStaffNum=numberOnDuty;
            currentStaffNum = 0;
            staff=new ArrayList<Staff>();
            workUnits=new ArrayList<>();
            for(int i=index;i<index+unitNum&&i<flowUnits.size();i++){
                Date begin=flowUnits.get(i).getBeginAt();
                TimeStaffNum.WorkUnit unit ;
                if(flowUnits.get(i).getFlow()==-1) unit=new WorkUnit(begin,new LinkedList<>(),0,prepare);
                else if(flowUnits.get(i).getFlow()==-2) unit=new WorkUnit(begin,new LinkedList<>(),0,closing);
                else unit=new TimeStaffNum.WorkUnit(begin,new LinkedList<>(),flowUnits.get(i).getFlow()/maxServiceNumber);
                workUnits.add(unit);
            }
        }
        @Data
        public class WorkUnit{
            private Date beginTime;
            private LinkedList<Staff> staffs;
            private int minStaffNum;
            private int currentNum;
            private String position;
            public WorkUnit(Date beginTime, LinkedList<Staff> staffs, double min){
                this.beginTime=beginTime;
                this.staffs=staffs;
                minStaffNum=(int)min;
                if(beginTime!=null&&minStaffNum==0) minStaffNum=numberOnDuty;
                currentNum=0;
                position=null;
            }
            public WorkUnit(Date beginTime, LinkedList<Staff> staffs, double min,String position){
                this.beginTime=beginTime;
                this.staffs=staffs;
                minStaffNum=(int)min;
                if(beginTime!=null&&minStaffNum==0) minStaffNum=numberOnDuty;
                currentNum=0;
                this.position=position;
            }
            public void add(Staff s){
                if(s==null) return;
                if(this.staffs==null) return;
                if(this.beginTime==null) return;
                if(this.contains(s)) return;
                currentNum++;
                staffs.add(s);
                s.setDayWorkTime(0.5);
            }
            public void add(int index, Staff s){
                if(s==null) return;
                if(this.beginTime==null) return;
                currentNum++;
                staffs.add(0,s);
                s.setDayWorkTime(0.5);
            }
            public void remove(Staff s){
                if(s==null) return;
                if(beginTime==null) return;
                currentNum--;
                staffs.remove(s);
                s.setDayWorkTime(-0.5);
            }
            public boolean contains(Staff staff){
                if(staffs==null) return false;
                return staffs.contains(staff);
            }
            public boolean isFull(){
                return minStaffNum<=currentNum;
            }
            public void move(Staff staff, List<TimeStaffNum> timeStaffNumList, int index){
                TimeStaffNum.WorkUnit newUnit=null;
                for(int i=index;i< timeStaffNumList.size();i++){
                    for(TimeStaffNum.WorkUnit unit:timeStaffNumList.get(i).workUnits)
                        if(!unit.contains(staff)){
                            newUnit=unit;
                            break;
                        }
                    if(newUnit!=null) break;
                }
                if(newUnit!=null) newUnit.add(0, staff);
                else return;
                remove(staff);
            }
            public void swap(Staff former, Staff later, List<TimeStaffNum> timeStaffNumList, int index, int indexOfUnit, int length){
                int indexOfOld1=0,indexOfOld2=0;
                if(index<0) return;
                if(indexOfUnit==unitNum-1) indexOfOld1 += 1;
                for(int i=0;i<length;i++){
                    if(index<=0) continue;
                    if(index>=timeStaffNumList.size()) continue;
                    if(indexOfUnit==0){
                        timeStaffNumList.get(index).workUnits.get(indexOfUnit).remove(former);
                        timeStaffNumList.get(index).workUnits.get(indexOfUnit).add(later);
                        index--;
                        indexOfUnit=unitNum-1;
                        continue;
                    }
                    else {
                        timeStaffNumList.get(index).workUnits.get(indexOfUnit).remove(former);
                        timeStaffNumList.get(index).workUnits.get(indexOfUnit).add(later);
                        indexOfUnit--;
                    }
                    if(indexOfOld2==unitNum-1){
                        timeStaffNumList.get(indexOfOld1).workUnits.get(indexOfOld2).remove(later);
                        timeStaffNumList.get(indexOfOld1).workUnits.get(indexOfOld2).add(former);
                        index++;
                        indexOfUnit=0;
                    }
                    else{
                        timeStaffNumList.get(indexOfOld1).workUnits.get(indexOfOld2).remove(later);
                        timeStaffNumList.get(indexOfOld1).workUnits.get(indexOfOld2).add(former);
                        indexOfOld2++;
                    }
                }
            }
            public void replace(Staff newOne, Staff oldOne, List<TimeStaffNum> timeStaffNumList, int index){
                for(int i=timeStaffNumList.get(index).workUnits.indexOf(this);i<timeStaffNumList.get(index).workUnits.size();i++){
                    if(i==-1) break;
                    timeStaffNumList.get(index).workUnits.get(i).remove(oldOne);
                    timeStaffNumList.get(index).workUnits.get(i).add(0,newOne);
                }
                for(int i=index+1;i<timeStaffNumList.size();i++){
                    for(TimeStaffNum.WorkUnit unit:timeStaffNumList.get(i).workUnits){
                        if(!unit.contains(oldOne)) return;
                        unit.remove(oldOne);
                        unit.add(0,newOne);
                    }
                }
            }
            public void replace(Staff newOne, Staff oldOne, List<TimeStaffNum> timeStaffNumList, int index, int length){
                TimeStaffNum timeStaffNum=timeStaffNumList.get(index);
                int count=0;
                int indexOfUnit=timeStaffNum.workUnits.indexOf(this);
                if(indexOfUnit==timeStaffNum.workUnits.size()-1) {
                    //if(index==)
                    timeStaffNum=timeStaffNumList.get(index+1);
                    for(TimeStaffNum.WorkUnit unit:timeStaffNum.workUnits){
                        unit.remove(oldOne);
                        unit.add(0,newOne);
                        count++;
                        if(count==length) break;
                    }
                    if(length>count) timeStaffNum.workUnits.get(timeStaffNum.workUnits.size()-1).replace(newOne,oldOne,timeStaffNumList,index+1,count-length);
                }
                else {
                    ArrayList<TimeStaffNum.WorkUnit> workUnits=timeStaffNum.workUnits;
                    for(int i=indexOfUnit+1;i<workUnits.size()-1;i++){
                        workUnits.get(i).remove(oldOne);
                        workUnits.get(i).add(0,newOne);
                        count++;
                        if(count==length) break;
                    }
                    if(length>count) timeStaffNum.workUnits.get(workUnits.size()-1).replace(newOne,oldOne,timeStaffNumList,index,count-length);
                }
            }
        }
        public void addUnit(List<TimeStaffNum> timeStaffNumList,int direct){
            if(direct<0){
                if(timeStaffNumList.get(0).workUnits.isEmpty()) workUnits.add(0,new WorkUnit(new Date(timeStaffNumList.get(1).startTime.getTime()-1800000),new LinkedList<>(),numberOnDuty));
                else workUnits.add(0,new WorkUnit(new Date(timeStaffNumList.get(0).workUnits.get(0).beginTime.getTime()-1800000),new LinkedList<>(),numberOnDuty));
                startTime.setTime(startTime.getTime()-1800000);
            }
            else{
                int last=timeStaffNumList.size()-1;
                if(timeStaffNumList.get(last).workUnits.isEmpty()) workUnits.add(new WorkUnit(new Date(timeStaffNumList.get(last).startTime.getTime()),new LinkedList<>(),numberOnDuty));
                else workUnits.add(new WorkUnit(new Date(timeStaffNumList.get(last).workUnits.get(workUnits.size()-1).beginTime.getTime()+1800000),new LinkedList<>(),numberOnDuty));
            }
        }
        public void add(Staff s){
            currentStaffNum+=1;
            if(staff.contains(s)) return;
            staff.add(s);
            for(TimeStaffNum.WorkUnit workUnit:workUnits) workUnit.add(s);
        }
        public boolean isFull(){
            return minStaffNum <= currentStaffNum;
        }
        public boolean contains(Staff staff){
            for(TimeStaffNum.WorkUnit unit:workUnits){
                if(unit.contains(staff)) return true;
            }
            return false;
        }
        public void format(int direct){
            if(workUnits.size()==unitNum) return;
            int length=unitNum- workUnits.size();
            if(direct>0){
                for(int i=0;i<length;i++){
                    workUnits.add(new WorkUnit(null,new LinkedList<>(),0));
                }
            }
            else{
                for(int i=0;i<length;i++){
                    workUnits.add(0,new WorkUnit(null,new LinkedList<>(),0));
                    startTime.setTime(startTime.getTime()-1800000);
                }
            }
        }
    }
    private class Staff{       //方便使用，替换employee
        private double dayWorkTime,weekWorkTime,continuousWorkTime,monthWorkTime;
        private int continuousWorkDay;
        private Long id;
        private String position;
        private Prefer prefer;

        public Staff(Long id){
            this.id=id;
            dayWorkTime=0;
            weekWorkTime=0;
            continuousWorkTime=0;
            position=null;
            monthWorkTime=0;
            continuousWorkDay=0;
        }
        public Staff(Employee e){
            this(e.getId());
            Preference preference=(Preference) preferenceService.getPreference(this.id).getData();
            prefer=new Prefer(preference);
            if(prefer.durationOfShift==null) prefer.durationOfShift=8;
            if(prefer.durationOfWeek==null) prefer.durationOfWeek=40;
            position=e.getPosition();
        }
        public Staff(){}
        public boolean isTired(){
            if(weekWorkTime>=prefer.durationOfWeek) return true;
            if(dayWorkTime>=prefer.durationOfShift) return true;
            if(continuousWorkTime>=4) return true;
            return false;
        }
        public void setDayWorkTime(double time){
            weekWorkTime+=time;
            dayWorkTime+=time;
            monthWorkTime+=time;
            continuousWorkTime+=time;
            if(continuousWorkTime<0) continuousWorkTime=0;
        }
        //员工的当前工作时间段中 该小段时间相对整段时间的位置是否在中间
        public boolean isMiddle(List<TimeStaffNum> timeStaffNumList, int indexOfList, TimeStaffNum.WorkUnit unit){
            int indexOfUnits=timeStaffNumList.get(indexOfList).workUnits.indexOf(unit);
            if(indexOfList==0&&indexOfUnits==0||indexOfList==timeStaffNumList.size()-1&&indexOfUnits==unitNum-1) return false;
            if(indexOfUnits==0){
                return timeStaffNumList.get(indexOfList-1).workUnits.get(unitNum-1).contains(this)&&timeStaffNumList.get(indexOfList).workUnits.get(1).contains(this);
            }
            else if(indexOfUnits==unitNum-1)
                return timeStaffNumList.get(indexOfList).workUnits.get(indexOfUnits-1).contains(this)&&timeStaffNumList.get(indexOfList+1).workUnits.get(0).contains(this);
            else return timeStaffNumList.get(indexOfList).workUnits.get(indexOfUnits-1).contains(this)&&timeStaffNumList.get(indexOfList).workUnits.get(indexOfUnits+1).contains(this);
        }
        //员工的当前工作时间段中 该小段时间相对整段时间的位置是否在末尾
        public boolean isLast(List<TimeStaffNum> timeStaffNumList, int indexOfList, TimeStaffNum.WorkUnit unit){
            int indexOfUnits=timeStaffNumList.get(indexOfList).workUnits.indexOf(unit);
            if(indexOfList==timeStaffNumList.size()-1&&indexOfUnits==unitNum-1) return true;
            if(indexOfUnits==unitNum-1){
                return !timeStaffNumList.get(indexOfList+1).workUnits.get(0).contains(this);
            }
            else return !timeStaffNumList.get(indexOfList).workUnits.get(indexOfUnits+1).contains(this);
        }
        //寻找在当前时间段附近的工作时间最小的员工;index>0 向前;index<0 向后;
        private Staff findNearMin(List<TimeStaffNum> timeStaffNumList, int index){
            Staff min=new Staff();
            min.setDayWorkTime(40);
            if(index>0) {
                for(int i=index-1;i>-1;i--){
                    List<TimeStaffNum.WorkUnit> units = timeStaffNumList.get(i).workUnits;
                    for(int j=units.size()-1;j>-1;j--){
                        if(!units.get(j).contains(this))
                            for(Staff s:units.get(j).staffs){
                                if(units.get(j).position!=null&&units.get(j).position.equals(s.position)) continue;
                                if(s.dayWorkTime<min.dayWorkTime) min=s;
                                else if(s.dayWorkTime==min.dayWorkTime&&s.weekWorkTime<min.weekWorkTime) min=s;
                            }
                    }
                    if(min.dayWorkTime!=40) break;
                }
            }
            else{
                for(int i=-index+1;i<timeStaffNumList.size();i++){
                    List<TimeStaffNum.WorkUnit> units = timeStaffNumList.get(i).workUnits;
                    for (TimeStaffNum.WorkUnit unit : units) {
                        if (!unit.contains(this))
                            for (Staff s : unit.staffs) {
                                if (s.dayWorkTime < min.dayWorkTime) min = s;
                                else if (s.dayWorkTime == min.dayWorkTime && s.weekWorkTime < min.weekWorkTime) min = s;
                            }
                    }
                    if(min.dayWorkTime!=40) break;

                }
            }
            return min;
        }
        public List<Staff> toStaff(List<Employee> employees){
            List<Staff> staffs=new ArrayList<>();
            for(Employee employee:employees) staffs.add(new Staff(employee));
            return staffs;
        }
        public double getContinuousWorkTime(List<TimeStaffNum> timeStaffNumList, int index){
            int start=0,end= timeStaffNumList.size()-1;
            continuousWorkTime=0;
            for(int i=index-1;i>=0;i--){
                if(!timeStaffNumList.get(i).contains(this)){
                    start=i+1;
                    break;
                }
            }
            for(int i=index;i<timeStaffNumList.size();i++)
                if(!timeStaffNumList.get(i).contains(this)){
                    end=i-1;
                    if(index==timeStaffNumList.size()-1) end=i;
                    break;
                }
            for(int i=start;i<=end;i++){
                for(TimeStaffNum.WorkUnit unit:timeStaffNumList.get(i).workUnits){
                    if(unit.contains(this)) continuousWorkTime += 0.5;
                    else if(continuousWorkTime!=0) break;
                }
            }
            return continuousWorkTime;
        }
        public double getContinuousWorkTime(List<TimeStaffNum> timeStaffNumList,int index,int indexOfUnits){
            int start=-1,end=-1,startUnit=0,endUnit=0;
            continuousWorkTime=0;
            for(int i=index;i>=0;i--){
                if(!timeStaffNumList.get(i).contains(this)){
                    start=i+1;
                    startUnit=0;
                    break;
                }
                else {
                    int a = timeStaffNumList.get(i).workUnits.size()-1;
                    if(i==index) a=indexOfUnits;
                    for(int i1 = a;i1>=0;i1--){
                        if(!timeStaffNumList.get(i).workUnits.get(i1).contains(this)){
                            startUnit=i1+1;
                            start=i;
                            if(startUnit==timeStaffNumList.get(i).workUnits.size()) {
                                start=i+1;
                                startUnit=0;
                            }
                            break;
                        }
                    }
                    if(start!=-1) break;
                }
            }
            if(start==-1) start=0;

            for(int i=index;i<timeStaffNumList.size();i++){
                if(!timeStaffNumList.get(i).contains(this)){
                    end=i-1;
                    if(index==timeStaffNumList.size()-1) end=i;
                    endUnit=timeStaffNumList.get(end).workUnits.size()-1;
                    break;
                }
                else{
                    int a=0;
                    if(i==index) a=indexOfUnits;
                    for(int i1 = a;i1<timeStaffNumList.get(i).workUnits.size();i1++){
                        if(!timeStaffNumList.get(i).workUnits.get(i1).contains(this)){
                            endUnit=i1-1;
                            end=i;
                            if(endUnit==-1){
                                end=i-1;
                                endUnit=timeStaffNumList.get(end).workUnits.size()-1;
                            }
                            break;
                        }
                    }
                }
                if(end!=-1) break;
            }
            if(end==-1) {
                end = timeStaffNumList.size() - 1;
                endUnit=unitNum-1;
            }

            for(int i=start;i<=end;i++){
                int a=0,b=unitNum-1;
                if(i==start) a=startUnit;
                if(i==end) b=endUnit;
                for(int j=a;j<=b;j++){
                    continuousWorkTime+=0.5;
                }
            }
            return continuousWorkTime;
        }
        public void setContinuousWorkDay(List<TimeStaffNum> timeStaffNumList) {
            boolean contains=false;
            for(TimeStaffNum timeStaffNum:timeStaffNumList)
                if(timeStaffNum.contains(this)) {
                    contains = true;
                    break;
                }
            if(contains) continuousWorkDay++;
            else continuousWorkDay=0;
        }

        public boolean connect(List<TimeStaffNum> timeStaffNumList, int index, int indexOfUnits){
            if(indexOfUnits==2){
                if(timeStaffNumList.get(index).workUnits.get(indexOfUnits+1).contains(this)) return connect(timeStaffNumList,index,indexOfUnits+1);
                if(index==timeStaffNumList.size()-1) return false;
                if(timeStaffNumList.get(index+1).workUnits.get(0).contains(this)){
                    if(weekWorkTime<=prefer.durationOfWeek-0.5&&dayWorkTime<=prefer.durationOfShift-0.5&&getContinuousWorkTime(timeStaffNumList,index+1,0)<=3){
                        timeStaffNumList.get(index).workUnits.get(3).add(this);
                        return true;
                    }
                } else if (timeStaffNumList.get(index+1).workUnits.get(1).contains(this)) {
                    if(weekWorkTime<=prefer.durationOfWeek-1&&dayWorkTime<=prefer.durationOfShift-1&&getContinuousWorkTime(timeStaffNumList,index+1,0)<=2.5){
                        timeStaffNumList.get(index).workUnits.get(3).add(this);
                        timeStaffNumList.get(index+1).workUnits.get(1).add(this);
                        return true;
                    }
                }
            }
            else if(indexOfUnits==3){
                if(index==timeStaffNumList.size()-1) return false;
                if(timeStaffNumList.get(index+1).workUnits.get(0).contains(this)) return connect(timeStaffNumList,index+1,0);
                if(timeStaffNumList.get(index+1).workUnits.get(2).contains(this)){
                    if(weekWorkTime<=prefer.durationOfWeek-0.5&&dayWorkTime<=prefer.durationOfShift-0.5&&getContinuousWorkTime(timeStaffNumList,index+1,1)<=3){
                        timeStaffNumList.get(index+1).workUnits.get(0).add(this);
                        return true;
                    }
                } else if (timeStaffNumList.get(index+1).workUnits.get(3).contains(this)) {
                    if(weekWorkTime<=prefer.durationOfWeek-1&&dayWorkTime<=prefer.durationOfShift-1&&getContinuousWorkTime(timeStaffNumList,index+1,2)<=2.5){
                        timeStaffNumList.get(index+1).workUnits.get(0).add(this);
                        timeStaffNumList.get(index+1).workUnits.get(1).add(this);
                        return true;
                    }
                }
            }
            else if(indexOfUnits<2){
                if(timeStaffNumList.get(index).workUnits.get(indexOfUnits+1).contains(this)) return connect(timeStaffNumList,index,indexOfUnits+1);
                if(timeStaffNumList.get(index).workUnits.get(indexOfUnits+2).contains(this)){
                    if(weekWorkTime<=prefer.durationOfWeek-0.5&&dayWorkTime<=prefer.durationOfShift-0.5&&getContinuousWorkTime(timeStaffNumList,index,indexOfUnits+2)<=3){
                        timeStaffNumList.get(index).workUnits.get(indexOfUnits+1).add(this);
                        return true;
                    }
                }else if(indexOfUnits==0&&timeStaffNumList.get(index).workUnits.get(2).contains(this)){
                    if(weekWorkTime<=prefer.durationOfWeek-1&&dayWorkTime<=prefer.durationOfShift-1&&getContinuousWorkTime(timeStaffNumList, index, 2)<=2.5){
                        timeStaffNumList.get(index).workUnits.get(1).add(this);
                        timeStaffNumList.get(index).workUnits.get(2).add(this);
                        return true;
                    }
                }
                else if (index<timeStaffNumList.size()-1&&indexOfUnits == 1 && timeStaffNumList.get(index + 1).workUnits.get(0).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 1 && dayWorkTime <= prefer.durationOfShift - 1 && getContinuousWorkTime(timeStaffNumList, index + 1, 0) <= 2.5) {
                            timeStaffNumList.get(index).workUnits.get(2).add(this);
                            timeStaffNumList.get(index).workUnits.get(3).add(this);
                            return true;
                        }
                    }

            }
            return false;
        }
        public void extend(List<TimeStaffNum> timeStaffNumList,int index,int indexOfUnits,int length){  //当前位置 length<=2
            if(length<0||index>=timeStaffNumList.size()-2){
                if(length<0) length=-length;
                if(indexOfUnits==0){
                    index--;
                    indexOfUnits=unitNum;
                }
                if(index==0) return;
                if(timeStaffNumList.get(index).workUnits.get(indexOfUnits-1).contains(this)) extend(timeStaffNumList, index, indexOfUnits-1, -length);
                else {
                    if(indexOfUnits<=1){
                        if(timeStaffNumList.get(index-1).workUnits.get(unitNum-length).contains(this)) return;
                        else{
                            timeStaffNumList.get(index).workUnits.get(indexOfUnits-1).add(this);
                            length--;
                            if(length>0) extend(timeStaffNumList,index,indexOfUnits,-length);
                        }
                    }
                    else{
                        if(indexOfUnits==2&&length==2&&timeStaffNumList.get(index-1).workUnits.get(unitNum-1).contains(this)) return;
                        else if((indexOfUnits!=2||length!=2)&&timeStaffNumList.get(index).workUnits.get(indexOfUnits-length-1).contains(this)) return;
                        else{
                            timeStaffNumList.get(index).workUnits.get(indexOfUnits-1).add(this);
                            length--;
                            if(length>0) extend(timeStaffNumList,index,indexOfUnits,-length);
                        }
                    }
                }
            }
            else{
                if(indexOfUnits==3) {
                    index+=1;
                    indexOfUnits=-1;
                }
                if(index==timeStaffNumList.size()) return;
                if(indexOfUnits>=2){
                    if(timeStaffNumList.get(index+1).workUnits.get(indexOfUnits-3+length).contains(this)) extend(timeStaffNumList,index,indexOfUnits,-length);
                    else {
                        timeStaffNumList.get(index).workUnits.get(indexOfUnits+1).add(this);
                        length--;
                        if(length>0) extend(timeStaffNumList,index,indexOfUnits,length);
                    }
                }
                else{
                    if(indexOfUnits==1&&length==2&&timeStaffNumList.get(index+1).workUnits.get(0).contains(this)) extend(timeStaffNumList, index, indexOfUnits, -length);
                    else if((indexOfUnits!=1||length!=2)&&timeStaffNumList.get(index).workUnits.get(length+indexOfUnits+1).contains(this)) extend(timeStaffNumList,index,indexOfUnits,-length);
                    else{
                        timeStaffNumList.get(index).workUnits.get(indexOfUnits+1).add(this);
                        length--;
                        if(length>0) extend(timeStaffNumList,index,indexOfUnits,length);
                    }
                }
            }
        }
        public void init(){
            continuousWorkDay=0;
            monthWorkTime=0;
            dayWorkTime=0;
            weekWorkTime=0;
            continuousWorkTime=0;
        }
        public List<Staff> init(List<Staff> staffs){
            for(Staff staff:staffs) staff.init();
            return staffs;
        }
    }

    private class Prefer {
        private Long id;
        private List<Integer> workingDay;
        private List<Float> workingHours;
        private Integer durationOfShift;//每天时长
        private Integer durationOfWeek;

        public Prefer(Preference preference) {
            this.id = preference.getId();
            this.durationOfShift = preference.getDurationOfShift();
            this.durationOfWeek = preference.getDurationOfWeek();
            String s = preference.getWorkingDay();
            if (s != null) {
                String[] list = s.split(",");
                workingDay = new ArrayList<Integer>();
                for (String a : list) {
                    if (a.equals(" ") || a.equals("")) continue;
                    workingDay.add(Integer.parseInt(a));
                }
            }
            s = preference.getWorkingHours();
            if (s != null) {
                addWorkingHours(s);
            }
        }

        public Prefer() {}

        public List<Prefer> toPrefer(List<Preference> preferenceList) {
            var preferList = new ArrayList<Prefer>();
            preferenceList.forEach(a -> {
                preferList.add(new Prefer(a));
            });
            return preferList;
        }

        public void addWorkingHours(String s) {
            String[] list = s.split(",");
            workingHours = new ArrayList<>();
            int start, end;
            for (String s1 : list) {
                if (s1.equals(" ") || s1.equals("")) continue;
                start = Integer.parseInt(s1.substring(0, 2));
                end = Integer.parseInt(s1.substring(6, 8));
                if (s1.charAt(3) == '3') start += 0.5;
                if (s1.charAt(9) == '3') end += 0.5;
                for (float i = start; i <= end; i += 0.5) {
                    if (!workingHours.contains(i)) workingHours.add(i);
                }
            }
            workingHours.sort((a, b) -> (int) (a + a - b - b));
        }

        public boolean mateDay(int week) {
            if (workingDay == null) return true;
            for (int a : workingDay)
                if (a == week)
                    return true;
            return false;
        }

        public void mateTime(String start, String end, int dayOfWeek, Staff staff) {
            float matchDay = 0, matchHour = 0, matchlength = 0, match = 0;
            if (findStaff(id) == null) return;
            if(staff.continuousWorkDay>=maxWorkingDay) {
                matchingDegree.put(staff,match);
                return;
            }
            float startTime = Integer.parseInt(start.substring(0, 2));
            if (start.charAt(3) == '3') startTime += 0.5;
            float endTime = Integer.parseInt(end.substring(0, 2));
            if (end.charAt(3) == '3') endTime += 0.5;
            int k = -1;
            if (workingHours != null){
                for (int i = 0; i < workingHours.size(); i++) {
                    if (workingHours.get(i) == startTime) {
                        if (workingHours.size() - i <= unitNum) {
                            matchHour = (workingHours.size() - i) / (float) unitNum;
                        } else k = i;
                    }
                    if (k != -1 && endTime < workingHours.get(i)) {
                        matchHour = (i - k) / (float) unitNum;
                    }
                }
            }
            if(workingDay!=null){
                for (int i = 0; i < workingDay.size(); i++) {
                    if (workingDay.get(i) == dayOfWeek) {
                        matchDay = 1;
                        break;
                    }
                }
            }

            if (durationOfShift > staff.continuousWorkTime) matchlength = 1;
            match = (float) (0.2 * matchDay + 0.5 * matchHour + 0.3 * matchlength);
            if (staff.weekWorkTime == 0) match += 0.05;
            if(staff.monthWorkTime<monthMin) match+=0.05;
            matchingDegree.put(staff, match);
        }
    }
    private ArrayList<Preference> findPreference(List<Employee> list){
        var pList=new ArrayList<Preference>();
        list.forEach(e->{pList.add((Preference) preferenceService.getPreference(e.getId()).getData());});
        return pList;
    }
    private int atLeastNum(Flow flow,int index){  //一个flow为半小时，暂定两小时为一组
        double max;
        max= flow.getFlowUnits().get(index).getFlow();
        if(index<=flow.getFlowUnits().size()-unitNum){
            for(int i=index;i<index+unitNum;i++){
                if(flow.getFlowUnits().get(i).getFlow()>max) max=flow.getFlowUnits().get(i).getFlow();
            }
        }
        else {
            for(int i=index;i<flow.getFlowUnits().size();i++){
                if(flow.getFlowUnits().get(i).getFlow()>max) max=flow.getFlowUnits().get(i).getFlow();
            }
        }
        return (int)(max/maxServiceNumber);
    }
    public String getEndTime(String start){
        if(start==null) return null;
        int a=Integer.parseInt(start.substring(0,2))+2;
        if(a<10) return "0"+a+start.substring(2);
        else return a+start.substring(2);
    }
    public int getDayOfWeek(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)-1;
        if(dayOfWeek<1) dayOfWeek=7;
        return dayOfWeek;
    }
    public void countWorkTime(ArrayList<ArrayList<TimeStaffNum>> timeStaffNumList){
        for(Staff staff:staffList) {
            staff.weekWorkTime=0;
            staff.dayWorkTime=0;
            staff.monthWorkTime=0;
        }
        for(ArrayList<TimeStaffNum> timeStaffNums:timeStaffNumList){
            for(TimeStaffNum timeStaffNum:timeStaffNums){
                for(TimeStaffNum.WorkUnit workUnit:timeStaffNum.workUnits){
                    for(Staff staff:workUnit.staffs){
                        staff.setDayWorkTime(0.5);
                    }
                }
            }
            for(Staff staff:staffList) staff.dayWorkTime=0;
        }
    }
    public List<TimeStaffNum> setSpecialPosition(List<TimeStaffNum> timeStaffNumList){
        ArrayList<Staff> choice=new ArrayList<>();
        for(Staff staff:staffList) {
            if(prepare!=null&&prepare.equals(staff.position)) choice.add(staff);
            if(closing!=null&&closing.equals(staff.position)) choice.add(staff);
        }
        for(TimeStaffNum timeStaffNum:timeStaffNumList){
            for(TimeStaffNum.WorkUnit unit:timeStaffNum.workUnits){
                if(unit.position!=null){
                    while(!unit.isFull())
                        for(Staff staff:choice){
                            if(unit.position.equals(staff.position)&&!staff.isTired()) timeStaffNum.add(staff);
                            if(unit.isFull()) break;
                        }
                }
            }
        }
        return timeStaffNumList;
    }
    public List<TimeStaffNum> check(List<TimeStaffNum> timeStaffNumList){
        List<Staff> staffList=new LinkedList();
        for(int i = 0;i<timeStaffNumList.size();i++){
            int k=0;
            for(TimeStaffNum.WorkUnit workUnit:timeStaffNumList.get(i).workUnits){
                k=timeStaffNumList.get(i).workUnits.indexOf(workUnit);
                if(workUnit.beginTime!=null){
                    Set<Staff> staffSet=new HashSet<>(workUnit.staffs);
                    workUnit.staffs=new LinkedList<>(staffSet);
                    for(int j=0;j<workUnit.staffs.size();j++){
                        Staff staff=workUnit.staffs.get(j);
                        if(!staffList.contains(staff)){
                            double time= staff.getContinuousWorkTime(timeStaffNumList,i,k);
                            try {
                                if(time<=1) {
                                    if (staff.id == 0L) continue;
                                    if(!staff.connect(timeStaffNumList,i,k)){
                                        if(time==1&&staff.dayWorkTime<=staff.prefer.durationOfShift-1 && staff.weekWorkTime<=staff.prefer.durationOfWeek-1) staff.extend(timeStaffNumList,i,k,2);
                                        else {
                                            workUnit.remove(staff);
                                            workUnit.add(new Staff(0L));
                                            j--;
                                        }
                                    }
                                } else if (time>=1&&time<2) {
                                    if(staff.connect(timeStaffNumList,i,k)) {
                                    }
                                    else if(staff.dayWorkTime<=staff.prefer.durationOfShift-2+time && staff.weekWorkTime<=staff.prefer.durationOfWeek-2+time) staff.extend(timeStaffNumList,i,k, (int) ((2-time)*2));
                                    else{
                                        workUnit.remove(staff);
                                        workUnit.add(new Staff(0L));
                                        j--;
                                    }
                                }else {
                                    staffList.add(staff);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    for(int j=0;j< staffList.size();j++){
                        Staff staff=staffList.get(j);
                        if(!workUnit.contains(staff)) {
                            staffList.remove(staff);
                            j--;
                        }
                    }
                }
                k++;
            }
        }

        return timeStaffNumList;
    }
    public List<TimeStaffNum> init(Flow flow1,int dayOfWeek){
        Flow flow=new Flow(flow1);
        ArrayList<Flow.FlowUnit> units = flow.getFlowUnits();
        Flow.FlowUnit last=units.get(0);
        for(int i=0;i<prepareTime*2;i++){
            Flow.FlowUnit unit = new Flow.FlowUnit(new Date(last.getBeginAt().getTime()-1800000),new Date(last.getEndAt().getTime()-1800000),-1);
            units.add(0,unit);
            last=unit;
        }
        last=units.get(units.size()-1);
        for(int i=0;i<closingTime*2;i++){
            Flow.FlowUnit unit = new Flow.FlowUnit(new Date(last.getBeginAt().getTime()+1800000),new Date(last.getEndAt().getTime()+1800000),-2);
            units.add(unit);
            last=unit;
        }
        List<TimeStaffNum> timeStaffNumList=new ArrayList<>();
        for(int i=0;i<flow.getFlowUnits().size();i+=unitNum){
            timeStaffNumList.add(new TimeStaffNum(flow.getFlowUnits(),i,atLeastNum(flow,i),unitNum));
        }
        return timeStaffNumList;
    }
    public List<ArrayList<TimeStaffNum>> arrangeMonth(long shopId,List<Flow> flows,long ruleId){
        List<ArrayList<Arranger.TimeStaffNum>> timeStaffNumList = new ArrayList<>();
        Rule rule = (Rule) ruleService.getRule(ruleId).getData();
        setRule(rule);
        List<EmployeeVO> employeeVoList = (List<EmployeeVO>) employeeService.getEmployeeByShop(shopId).getData();
        List<Employee> employeeList = transTo(employeeVoList);
        if(employeeList ==null) return null;
        List<Preference> preferenceList = findPreference(employeeList);
        preference=new Prefer().toPrefer(preferenceList);
        staffList=new Staff().toStaff(employeeList);
        for (int i = 0; i < (flows.size() - 1) / 7 + 1; i++) {
            if (i * 7 + 7 > flows.size())
                timeStaffNumList.addAll(arrangeWeek(shopId, flows.subList(i * 7, flows.size()), ruleId));
            else timeStaffNumList.addAll(arrangeWeek(shopId, flows.subList(i * 7, (i + 1) * 7), ruleId));
            System.out.println("完成本周排班，起始日期为" + flows.get(i).getDate());
        }
        return timeStaffNumList;
    }
    public List<ArrayList<TimeStaffNum>> arrangeWeek(long shopId, List<Flow> flowsOfWeek, long ruleId) throws RuntimeException{
        ArrayList<ArrayList<TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        System.out.println("开始排班，本周排班起始星期为星期"+getDayOfWeek(flowsOfWeek.get(0).getDate()));
        int errorTime=0;
        for(int i=0;i< flowsOfWeek.size();i++) {
            try {
                Flow flow=flowsOfWeek.get(i);
                timeStaffNumList.add((ArrayList<TimeStaffNum>) newArrange(flow));
            }catch (IndexOutOfBoundsException e){
                System.out.println("重新开始本日排班");
                i--;
                errorTime++;
                countWorkTime(timeStaffNumList);
            }catch (RuntimeException e){
                System.out.println(e);
                errorTime++;
                i--;
                if(errorTime>10) {
                    e.printStackTrace();
                    System.out.println("排班失败，请根据控制台信息检查原因");
                }
                else if(errorTime==5) {
                    System.out.println("排班超时，重新开始本次排班");
                    System.out.println("本次排班起始星期为星期"+getDayOfWeek(flowsOfWeek.get(0).getDate()));
                    i=-1;
                    timeStaffNumList.clear();
                    staffList=new Staff().init(staffList);
                }
                else {
                    System.out.println("重新开始本日排班");
                    countWorkTime(timeStaffNumList);
                }
            }
        }

        return timeStaffNumList;
    }
    //完全按照所给的客流量排班
    public List<TimeStaffNum> newArrange(Flow flow) throws RuntimeException{
        int dayOfWeek=getDayOfWeek(flow.getDate());
        for(Staff staff:staffList) staff.dayWorkTime=0;
        List<TimeStaffNum> timeStaffNumList;
        timeStaffNumList=init(flow,dayOfWeek);
        System.out.println("开始本日排班，星期为星期"+dayOfWeek);
        //获取初始结果
        int index=0;
        int t=0,last1= timeStaffNumList.size()-1;
        matchingDegree=new HashMap<>();

        timeStaffNumList=setSpecialPosition(timeStaffNumList);      //特定岗位的时间段优先排班

        while(index<=last1) {
            if(t>500) throw new RuntimeException("排班超时,搜索次数t="+t+",超时位置dayOfWeek="+dayOfWeek+",indexOfTimeList="+index+",还需要"+(timeStaffNumList.get(index).minStaffNum-timeStaffNumList.get(index).currentStaffNum));
            matchingDegree.clear();
            TimeStaffNum timeStaffNum=timeStaffNumList.get(index);
            String start = timeStaffNumList.get(0).startTime.toString();
            String regEx = "\\d+\\d+:+\\d+\\d+:+\\d+\\d";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(start);
            if(!m.find()) System.out.println("not match");
            start = m.group().substring(0, 5);
            String end = getEndTime(start);
            for (Staff staff : staffList) {
                staff.prefer.mateTime(start,end,dayOfWeek,staff);
                if (timeStaffNum.isFull()) break;
            }
            if(t%2==1)
                for(Staff staff:staffList) staff.getContinuousWorkTime(timeStaffNumList,index);
            List<Staff> keySetList= new ArrayList<>(matchingDegree.keySet());

            // 均衡排班
            if(balanced)
                keySetList.sort((a,b)->{
                    if(Objects.equals(matchingDegree.get(b), matchingDegree.get(a))) return (int)(a.dayWorkTime*2-b.dayWorkTime*2);
                    else return (int)(matchingDegree.get(b)*100-matchingDegree.get(a)*100);
                });
            else{
                keySetList.sort((a,b)-> (int)(matchingDegree.get(b)*100-matchingDegree.get(a)*100));
            }
            for(int i=0;!timeStaffNum.isFull()&&i<matchingDegree.size();i++){
                if(!keySetList.get(i).isTired()) timeStaffNum.add(keySetList.get(i));
            }
            //抓壮丁
            if (!timeStaffNum.isFull()) {
                for (Prefer prefer : preference) {
                    Staff staff=findStaff(prefer.id);
                    if (prefer.mateDay(dayOfWeek)) {
                        if(staff!=null&&!hasSelected(staff,timeStaffNumList,index)&&!staff.isTired()) timeStaffNum.add(staff);
                    }
                    //强抓壮丁
                    if(t>20) {
                        if(staff!=null&& !hasSelected(staff, timeStaffNumList, index) &&!staff.isTired()) timeStaffNum.add(staff);
                    }
                    if (timeStaffNum.isFull()) break;
                }
            }
            t++;
            if(timeStaffNum.isFull()) index++;
        }

        //优化
        //优化过程尽量只改动已经排上的人
        //在已有基础上进行时间上的增删改
        //优化过程将不考虑员工工作时间偏好
        Staff last=null;
        int nextI=0;
        t=0;
        for(int i=0;i<timeStaffNumList.size();i++){
            if(t>1000) throw new RuntimeException("排班优化超时,位置为i="+i+",j="+(nextI+1));
            nextI=i;
            for(TimeStaffNum.WorkUnit unit:timeStaffNumList.get(i).workUnits){
                if(unit.minStaffNum<unit.currentNum){
                    boolean next=false;
                    //以下三块内容对于处理人流高峰期及以前的准备时间有效
                    //直接移除拥有较长连续工作时间的员工
                    for(Staff s:unit.staffs){
                        if(s.getContinuousWorkTime(timeStaffNumList,i)==0.5){
                            unit.remove(s);
                            next=true;
                            break;
                        }
                        if(s.equals(last)) continue;
                        if(s.getContinuousWorkTime(timeStaffNumList,i)>2&&!s.isMiddle(timeStaffNumList,i,unit)){
                            unit.remove(s);
                            next=true;
                            last=s;
                            break;
                        }
                    }
                    if(next) continue;
                    //对连续工作时间为两小时的员工进行处理，整体工作时间后移半小时
                    Staff inconformity=unit.staffs.get(unit.staffs.size()-1);
                    if(inconformity.getContinuousWorkTime(timeStaffNumList,i)<=2) {
                        if(!inconformity.isLast(timeStaffNumList,i,unit)) {
                            unit.move(inconformity,timeStaffNumList,i);
                            if(unit.contains(inconformity)) unit.replace(last,inconformity,timeStaffNumList,i);
                            else last=inconformity;
                        }
                        else{
                            unit.remove(inconformity);
                            last=inconformity;
                        }
                    }
                    //对连续工作时间大于两小时的员工，直接移除该时间工作
                    else if(inconformity.getContinuousWorkTime(timeStaffNumList,i)>2&&!inconformity.isMiddle(timeStaffNumList,i,unit)){
                        unit.remove(inconformity);
                        last=inconformity;
                    }
                }
                //处理工作时长不足两小时的员工
                int j=i+1;
                for(int i1=0;i1<unit.staffs.size();i1++){
                    Staff staff=unit.staffs.get(i1);
                    double time1=staff.getContinuousWorkTime(timeStaffNumList,i);
                    if(time1<2){
                        Staff minOne=staff.findNearMin(timeStaffNumList,i);
                        unit.replace(minOne,staff,timeStaffNumList,i);
                    }
                    //处理工作时长大于4小时的员工（要休息）
                    if(time1>4){
                        boolean next=false;
                        for(j=i;j>-1;j--){
                            TimeStaffNum tsm=timeStaffNumList.get(j);
                            for(int j1=tsm.workUnits.size()-1;j1>-1;j1--){
                                if(!tsm.workUnits.get(j1).contains(staff)){
                                    Staff newOne=staff.findNearMin(timeStaffNumList,i);//往前找，从前向后覆盖
                                    double time2=newOne.getContinuousWorkTime(timeStaffNumList,j);
                                    if(time2<4)
                                        timeStaffNumList.get(j).workUnits.get(j1).replace(newOne,staff,timeStaffNumList,j,(int)((time1-4)*2));
                                    else
                                        timeStaffNumList.get(j).workUnits.get(j1).swap(newOne,staff,timeStaffNumList,j,j1,(int)time2);
                                    next=true;
                                    break;
                                }
                            }
                            if(next) break;
                        }
                        if(j<0&&!next){
                            for(int j2 = i; j2 < timeStaffNumList.size(); j2++){
                                TimeStaffNum tsm=timeStaffNumList.get(j2);
                                for(int j1=0;j1<tsm.workUnits.size();j1++){
                                    if(!tsm.workUnits.get(j1).contains(staff)){
                                        Staff newOne=staff.findNearMin(timeStaffNumList,-i);
                                        double time2=newOne.getContinuousWorkTime(timeStaffNumList,j2);
                                        if(time2<4)
                                            timeStaffNumList.get(j2).workUnits.get(j1).replace(newOne,staff,timeStaffNumList, j2,(int)((time1-4)*2));
                                        else
                                            timeStaffNumList.get(j2).workUnits.get(j1).swap(staff,newOne,timeStaffNumList,j2,j1,(int)(time2));
                                        next=true;
                                        break;
                                    }
                                }
                                if(next) break;
                            }
                        }
                    }
                }
                nextI=j-1;
                if(nextI<0) nextI=0;
            }
            i=nextI;
            t++;
        }
        System.out.println("完成本次排班,星期为星期"+dayOfWeek);

        timeStaffNumList=check(timeStaffNumList);
        for(Staff staff:staffList) staff.setContinuousWorkDay(timeStaffNumList);
        //return timeStaffNumList;
        return timeStaffNumList;
    }
    public ArrayList<Employee> transTo(List<EmployeeVO> employeeVOs){
        ArrayList<Employee> employees=new ArrayList<>();
        for(EmployeeVO employeeVO:employeeVOs) employees.add(new Employee(employeeVO.getId(),employeeVO.getUid(),employeeVO.getPosition(),employeeVO.getShop(),employeeVO.getSalary(),employeeVO.getTime()));
        return employees;
    }
    public ArrayList<Long> transBack(LinkedList<Staff> staffs){
        ArrayList<Long> employees=new ArrayList<>();
        for(Staff staff:staffs) employees.add(staff.id);
        return employees;
    }
    //将排班信息转格式并存入数据库
    public long outPut(List<List<TimeStaffNum>> timeStaffNumList, long shopId, long ruleId, long managerId){
        ArrayList<Schedule.Week> weeks=new ArrayList<>();
        for(int i=0 ;i<(timeStaffNumList.size()-1)/7+1;i++){
            Schedule.Week week=new Schedule.Week();
            week.setStartAt(timeStaffNumList.get(i).get(0).startTime);
            if(i+6<timeStaffNumList.size()) week.setEndAt(timeStaffNumList.get(i+6).get(0).startTime);
            else week.setEndAt(timeStaffNumList.get(timeStaffNumList.size()-1).get(0).startTime);
            Schedule.WorkUnit[][] workUnits=new Schedule.WorkUnit[7][timeStaffNumList.get(0).size()*unitNum];
            for(int j=0;j<7;j++){
                if(timeStaffNumList.size() <=7*i+j) break;
                int halfHourNum=timeStaffNumList.get(i+j).size()*unitNum-unitNum+timeStaffNumList.get(i+j).get(timeStaffNumList.get(i+j).size()-1).workUnits.size();
                for(int k=0;k<halfHourNum;k++){
                    workUnits[j][k]=new Schedule.WorkUnit();
                    workUnits[j][k].setBeginTime(timeStaffNumList.get(7*i+j).get(k/unitNum).workUnits.get(k%unitNum).beginTime);
                    workUnits[j][k].setEmployees(transBack(timeStaffNumList.get(7*i+j).get(k/unitNum).workUnits.get(k%unitNum).staffs));
                }
            }
            week.setData(workUnits);
            weeks.add(week);
        }
        Schedule schedule=new Schedule(null,shopId, managerId,new Date(),true,ruleId,timeStaffNumList.get(0).get(0).startTime,timeStaffNumList.get(timeStaffNumList.size()-1).get(0).startTime,weeks);
        scheduleMapper.insert(schedule);
        System.out.println("新的排班数据已创建,id="+schedule.getId());
        return schedule.getId();
    }

}