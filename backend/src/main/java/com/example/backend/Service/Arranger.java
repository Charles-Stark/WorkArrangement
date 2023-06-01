package com.example.backend.Service;

import com.alibaba.fastjson.JSON;
import com.example.backend.POJO.*;
import com.example.backend.VO.EmployeeVO;
import com.example.backend.mapper.ScheduleMapper;
import lombok.Data;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private Map<Staff,Float>matchingDegree=new HashMap<>();;
    private ArrayList<String> prepare,closing,service;
    private boolean balanced=false;
    private List<Staff> candidate;
    public void setRule(Rule rule){ //原始客流量预测包含1小时准备时间，不包含收尾时间，即时间为8:00-21:00，不包含21:00之后的半小时
        prepare=new ArrayList<>();
        closing=new ArrayList<>();
        service=new ArrayList<>();
        prepare.add("prepare");
        closing.add("closing");
        service.add("service");
        if (rule==null) return;
        if(rule.getPrepareTime()!=null) prepareTime = rule.getPrepareTime();
        if(rule.getClosingTime()!=null) closingTime = rule.getClosingTime();
        if(rule.getNumberOnDuty()!=null) this.numberOnDuty=rule.getNumberOnDuty();
        if(rule.getMaxServiceNumber()!=null) this.maxServiceNumber=rule.getMaxServiceNumber();
        if(rule.getBalance()!=null) this.balanced=rule.getBalance();
        if(rule.getClosingPosition()!=null) {
            String[] positions=rule.getClosingPosition().trim().split(",");
            closing.addAll(Arrays.asList(positions));
        }
        if(rule.getPreparePosition()!=null) {
            String[] positions=rule.getPreparePosition().trim().split(",");
            prepare.addAll(Arrays.asList(positions));
        }
        if(rule.getServicePosition()!=null) {
            String[] positions=rule.getServicePosition().trim().split(",");
            service.addAll(Arrays.asList(positions));
        }
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
                else unit=new TimeStaffNum.WorkUnit(begin,new LinkedList<>(),flowUnits.get(i).getFlow()/maxServiceNumber,service);
                workUnits.add(unit);
            }
        }
        public TimeStaffNum(List<Schedule.WorkUnit> hours){
            if(hours.get(0)==null) return;
            minStaffNum=0;
            currentStaffNum=0;
            startTime= hours.get(0).getBeginTime();
            staff=new ArrayList<>();
            workUnits=new ArrayList<>();
            for(Schedule.WorkUnit unit: hours){
                if(unit==null) continue;
                workUnits.add(new WorkUnit(unit.getBeginTime(),new Staff().toStaff(unit.getEmployees()),unit.getEmployees().size()));
            }
        }
        public ArrayList<TimeStaffNum> getTimeStaffNumList(Schedule.WorkUnit[] day){
            ArrayList<TimeStaffNum> timeStaffNumList=new ArrayList<>();
            for(int i=0;i<day.length;i+=4){
                if (i + 4 < day.length)
                    timeStaffNumList.add(new TimeStaffNum(Arrays.stream(day).toList().subList(i, i + 4)));
                else timeStaffNumList.add(new TimeStaffNum(Arrays.stream(day).toList().subList(i, day.length)));
            }
            return timeStaffNumList;
        }
        @Data
        public class WorkUnit{
            private Date beginTime;
            private LinkedList<Staff> staffs;
            private int minStaffNum;
            private int currentNum;
            private ArrayList<String> position;
            public WorkUnit(Date beginTime, LinkedList<Staff> staffs, double min){
                this.beginTime=beginTime;
                this.staffs=staffs;
                minStaffNum=(int)min;
                if(beginTime!=null&&minStaffNum==0) minStaffNum=numberOnDuty;
                currentNum=0;
                position=null;
            }
            public WorkUnit(Date beginTime, LinkedList<Staff> staffs, double min,ArrayList<String> position){
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
            public boolean haveStaff(Staff staff) {
                return staffs.contains(staff);
            }
            public boolean haveStaff(long id){
                for(Staff staff1:staffs){
                    if(staff1.id==id) return true;
                }
                return false;
            }
        }
        public void add(Staff s){
            currentStaffNum+=1;
            if(staff.contains(s)) return;
            staff.add(s);
            candidate.remove(s);
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
        public boolean haveStaff(Staff staff){
            boolean have=false;
            for(WorkUnit unit:workUnits){
                if(unit.haveStaff(staff)){
                    have=true;
                    break;
                }
            }
            return have;
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
            prefer=new Prefer();
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
        public void setId(long id){
            this.id=id;
        }
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
        public Prefer getPrefer(){
            return prefer;
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
            Staff min=this;
            min.setDayWorkTime(40);
            if(index>0) {
                for(int i=index-1;i>-1;i--){
                    List<TimeStaffNum.WorkUnit> units = timeStaffNumList.get(i).workUnits;
                    for(int j=units.size()-1;j>-1;j--){
                        if(!units.get(j).contains(this))
                            for(Staff s:units.get(j).staffs){
                                if(units.get(j).position!=null&&!units.get(j).position.get(0).equals("service")&&units.get(j).position.contains(s.position)) continue;
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
                                if(unit.position!=null&&!unit.position.get(0).equals("service")&&unit.position.contains(s.position)) continue;
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
        public LinkedList<Staff> toStaff(ArrayList<Long> employees){
            List<EmployeeVO> employeeVOList=new ArrayList<>();
            for(Long id:employees)
                employeeVOList.add((EmployeeVO) employeeService.getEmployee(id).getData());
            return new LinkedList<>(this.toStaff(transTo(employeeVOList)));
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
            try {
                if (indexOfUnits == 2) {
                    if (timeStaffNumList.get(index).workUnits.get(indexOfUnits + 1).contains(this))
                        return connect(timeStaffNumList, index, indexOfUnits + 1);
                    if (index == timeStaffNumList.size() - 1) return false;
                    if (timeStaffNumList.get(index + 1).workUnits.get(0).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 0.5 && dayWorkTime <= prefer.durationOfShift - 0.5 && getContinuousWorkTime(timeStaffNumList, index + 1, 0) <= 3) {
                            timeStaffNumList.get(index).workUnits.get(3).add(this);
                            return true;
                        }
                    } else if (timeStaffNumList.get(index + 1).workUnits.get(1).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 1 && dayWorkTime <= prefer.durationOfShift - 1 && getContinuousWorkTime(timeStaffNumList, index + 1, 0) <= 2.5) {
                            timeStaffNumList.get(index).workUnits.get(3).add(this);
                            timeStaffNumList.get(index + 1).workUnits.get(1).add(this);
                            return true;
                        }
                    }
                } else if (indexOfUnits == 3) {
                    if (index == timeStaffNumList.size() - 1) return false;
                    if (timeStaffNumList.get(index + 1).workUnits.get(0).contains(this))
                        return connect(timeStaffNumList, index + 1, 0);
                    if (timeStaffNumList.get(index + 1).workUnits.get(2).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 0.5 && dayWorkTime <= prefer.durationOfShift - 0.5 && getContinuousWorkTime(timeStaffNumList, index + 1, 1) <= 3) {
                            timeStaffNumList.get(index + 1).workUnits.get(0).add(this);
                            return true;
                        }
                    } else if (timeStaffNumList.get(index + 1).workUnits.get(3).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 1 && dayWorkTime <= prefer.durationOfShift - 1 && getContinuousWorkTime(timeStaffNumList, index + 1, 2) <= 2.5) {
                            timeStaffNumList.get(index + 1).workUnits.get(0).add(this);
                            timeStaffNumList.get(index + 1).workUnits.get(1).add(this);
                            return true;
                        }
                    }
                } else if (indexOfUnits < 2) {
                    if (timeStaffNumList.get(index).workUnits.get(indexOfUnits + 1).contains(this))
                        return connect(timeStaffNumList, index, indexOfUnits + 1);
                    if (timeStaffNumList.get(index).workUnits.get(indexOfUnits + 2).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 0.5 && dayWorkTime <= prefer.durationOfShift - 0.5 && getContinuousWorkTime(timeStaffNumList, index, indexOfUnits + 2) <= 3) {
                            timeStaffNumList.get(index).workUnits.get(indexOfUnits + 1).add(this);
                            return true;
                        }
                    } else if (indexOfUnits == 0 && timeStaffNumList.get(index).workUnits.get(2).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 1 && dayWorkTime <= prefer.durationOfShift - 1 && getContinuousWorkTime(timeStaffNumList, index, 2) <= 2.5) {
                            timeStaffNumList.get(index).workUnits.get(1).add(this);
                            timeStaffNumList.get(index).workUnits.get(2).add(this);
                            return true;
                        }
                    } else if (index < timeStaffNumList.size() - 1 && indexOfUnits == 1 && timeStaffNumList.get(index + 1).workUnits.get(0).contains(this)) {
                        if (weekWorkTime <= prefer.durationOfWeek - 1 && dayWorkTime <= prefer.durationOfShift - 1 && getContinuousWorkTime(timeStaffNumList, index + 1, 0) <= 2.5) {
                            timeStaffNumList.get(index).workUnits.get(2).add(this);
                            timeStaffNumList.get(index).workUnits.get(3).add(this);
                            return true;
                        }
                    }

                }
            }catch (Exception e){
                return false;
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
                if(index<=0) return;
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

        public Prefer() {
            durationOfShift=8;
            durationOfWeek=40;
        }

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
                for (Integer integer : workingDay) {
                    if (integer == dayOfWeek) {
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
        int t=0;
        for(ArrayList<TimeStaffNum> timeStaffNums:timeStaffNumList){
            if(t%7==0) for(Staff staff:staffList) staff.weekWorkTime=0;
            for(TimeStaffNum timeStaffNum:timeStaffNums){
                if(timeStaffNum.workUnits==null) continue;
                for(TimeStaffNum.WorkUnit workUnit:timeStaffNum.workUnits){
                    for(Staff staff:workUnit.staffs){
                        staff.setDayWorkTime(0.5);
                    }
                }
            }
            for(Staff staff:staffList) staff.dayWorkTime=0;
            t++;
        }
    }
    public List<TimeStaffNum> setSpecialPosition(List<TimeStaffNum> timeStaffNumList){
        ArrayList<Staff> choice=new ArrayList<>();
        for(Staff staff:staffList) {
            if(prepare!=null&&prepare.contains(staff.position)) choice.add(staff);
            if(closing!=null&&closing.contains(staff.position)) choice.add(staff);
        }
        for(TimeStaffNum timeStaffNum:timeStaffNumList){
            for(TimeStaffNum.WorkUnit unit:timeStaffNum.workUnits){
                if(unit.position!=null){
                    for(Staff staff:choice){
                        if(unit.position.contains(staff.position)&&!staff.isTired()) timeStaffNum.add(staff);
                        if(unit.isFull()) break;
                    }
                }
            }
        }
        return timeStaffNumList;
    }
    //true,排班时调用;false,补全时调用
    public List<TimeStaffNum> check(List<TimeStaffNum> timeStaffNumList,boolean b,int index,int indexOfUnit) throws Exception {
        ArrayList<Staff> staffForSelect=new ArrayList<>();
        for(int i=0;i<10;i++) staffForSelect.add(new Staff((long) (-i)));
        LinkedList<Staff> staffList1 =new LinkedList<>();
        int checkTimes=0;
        for(int i = 0;i<timeStaffNumList.size();i++){
            if(!b) i=index;
            int k=0;
            for(k=0;k<timeStaffNumList.get(i).workUnits.size();k++){
            //for(TimeStaffNum.WorkUnit workUnit:timeStaffNumList.get(i).workUnits){
                if(!b) k=indexOfUnit;
                TimeStaffNum.WorkUnit workUnit=timeStaffNumList.get(i).workUnits.get(k);
                //k=timeStaffNumList.get(i).workUnits.indexOf(workUnit);
                if(workUnit.beginTime!=null){
                    Set<Staff> staffSet=new HashSet<>(workUnit.staffs);
                    workUnit.staffs=new LinkedList<>(staffSet);
                    for(int j=0;j<workUnit.staffs.size();j++){
                        checkTimes++;
                        if(checkTimes>5000) throw new Exception("结果优化错误");
                        Staff staff=workUnit.staffs.get(j);
                        if(staff.id==null) {
                            workUnit.remove(staff);
                            j--;
                            continue;
                        }
                        if(!staffList1.contains(staff)){
                            double time= staff.getContinuousWorkTime(timeStaffNumList,i,k);
                            try {
                                if(b&&staff.id<=0L) continue;
                                if(time<=1) {
                                    if(!staff.connect(timeStaffNumList,i,k)){
                                        if(time==1&&staff.dayWorkTime<=staff.prefer.durationOfShift-1 && staff.weekWorkTime<=staff.prefer.durationOfWeek-1) staff.extend(timeStaffNumList,i,k,2);
                                        else if(!b){
                                            staff.extend(timeStaffNumList,i,k, (int) (2*(2-time)));
                                        }
                                        else {
                                            workUnit.remove(staff);
                                            for(int t=0;t<10;t++){
                                                if(!workUnit.haveStaff(t)) {
                                                    workUnit.add(staffForSelect.get(t));
                                                    break;
                                                }
                                            }
                                            j--;
                                        }
                                    }
                                } else if (time>=1&&time<2) {
                                    if(staff.connect(timeStaffNumList,i,k)) {
                                        staffList1.add(staff);
                                    }
                                    else if(staff.id!=0L&&staff.dayWorkTime<=staff.prefer.durationOfShift-2+time && staff.weekWorkTime<=staff.prefer.durationOfWeek-2+time) staff.extend(timeStaffNumList,i,k, (int) ((2-time)*2));
                                    else if(!b)staff.extend(timeStaffNumList,i,k, (int) ((2-time)*2));
                                    else{
                                        workUnit.remove(staff);
                                        for(int t=0;t>-10;t--){
                                            if(!workUnit.haveStaff(t)) {
                                                workUnit.add(staffForSelect.get(t));
                                                break;
                                            }
                                        }
                                        j--;
                                    }
                                }else {
                                    staffList1.add(staff);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if(!b) return timeStaffNumList;
                        }
                    }
                    for(int j = 0; j< staffList1.size(); j++){
                        Staff staff= staffList1.get(j);
                        if(!workUnit.contains(staff)) {
                            staffList1.remove(staff);
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
        if(dayOfWeek>5){
            for(Flow.FlowUnit unit:flow.getFlowUnits()){
                unit.setBeginAt(new Date(unit.getBeginAt().getTime()+3600000));
                unit.setEndAt(new Date(unit.getEndAt().getTime()+3600000));
            }
        }
        List<TimeStaffNum> timeStaffNumList=new ArrayList<>();
        for(int i=0;i<flow.getFlowUnits().size();i+=unitNum){
            timeStaffNumList.add(new TimeStaffNum(flow.getFlowUnits(),i,atLeastNum(flow,i),unitNum));
        }
        System.out.println("首班时间:"+flow.getFlowUnits().get(0).getBeginAt());
        System.out.println("末班时间:"+flow.getFlowUnits().get(flow.getFlowUnits().size()-1).getEndAt());
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
        candidate=new LinkedList<>(staffList);

        for (int i = 0; i < (flows.size() - 1) / 7 + 1; i++) {
            if (i * 7 + 7 > flows.size())
                timeStaffNumList.addAll(arrangeWeek(flows.subList(i * 7, flows.size())));
            else timeStaffNumList.addAll(arrangeWeek(flows.subList(i * 7, (i + 1) * 7)));
            System.out.println("完成本周排班，起始日期为" + flows.get(i).getDate());
        }
        return timeStaffNumList;
    }
    public List<ArrayList<TimeStaffNum>> arrangeWeek(List<Flow> flowsOfWeek) throws RuntimeException{
        ArrayList<ArrayList<TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        System.out.println("开始排班，本周排班起始星期为星期"+getDayOfWeek(flowsOfWeek.get(0).getDate()));
        int errorTime=0;
        for(int i=0;i< flowsOfWeek.size();i++) {
            try {
                Flow flow=flowsOfWeek.get(i);
                timeStaffNumList.add((ArrayList<TimeStaffNum>) newArrange(flow));
                errorTime=0;
            }catch (Exception e){
                System.out.println(e);
                errorTime++;
                i--;
                if(errorTime>10) {
                    e.printStackTrace();
                    System.out.println("排班失败，请根据控制台信息检查原因");
                    break;
                }
                else if(errorTime==5) {
                    System.out.println("排班超时，重新开始本次排班");
                    System.out.println("本次排班起始星期为星期"+getDayOfWeek(flowsOfWeek.get(0).getDate()));
                    i=-1;
                    timeStaffNumList.clear();
                    new Staff().init(staffList);
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
        System.out.println("开始本日排班，星期为星期"+dayOfWeek);
        timeStaffNumList=init(flow,dayOfWeek);
        //获取初始结果
        int index=0;
        int t=0,last1= timeStaffNumList.size()-1;
        ArrayList<Staff> profit=new ArrayList<>();
        if(service.size()==1) profit.addAll(staffList);
        else
            for(Staff staff:staffList)
                if(service.contains(staff.position))
                    profit.add(staff);

        timeStaffNumList=setSpecialPosition(timeStaffNumList);      //准备时间和收尾时间，指定岗位优先排班

        while(index<=last1) {
            if(t>500) throw new RuntimeException("排班超时,搜索次数t="+t+",超时位置dayOfWeek="+dayOfWeek+",indexOfTimeList="+index+",还需要"+(timeStaffNumList.get(index).minStaffNum-timeStaffNumList.get(index).currentStaffNum));
            matchingDegree.clear();
            TimeStaffNum timeStaffNum=timeStaffNumList.get(index);
            String start = timeStaffNum.startTime.toString();
            String regEx = "\\d+\\d+:+\\d+\\d+:+\\d+\\d";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(start);
            if(!m.find()) System.out.println("not match");
            start = m.group().substring(0, 5);
            String end = getEndTime(start);
            for (Staff staff : staffList) {
                if(!profit.contains(staff)) continue;
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
                    if(t>200) {
                        if(staff!=null&&!staff.isTired()) timeStaffNum.add(staff);
                    }else if(t>20){
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
        try {
            for (int i = 0; i < timeStaffNumList.size(); i++) {
                if (t > 1000) throw new RuntimeException("排班优化超时,位置为i=" + i + ",j=" + (nextI + 1));
                nextI = i;
                for (TimeStaffNum.WorkUnit unit : timeStaffNumList.get(i).workUnits) {
                    if (unit.minStaffNum < unit.currentNum) {
                        boolean next = false;
                        //以下三块内容对于处理人流高峰期及以前的准备时间有效
                        //直接移除拥有较长连续工作时间的员工
                        for (Staff s : unit.staffs) {
                            if (s.getContinuousWorkTime(timeStaffNumList, i) == 0.5) {
                                unit.remove(s);
                                next = true;
                                break;
                            }
                            if (s.equals(last)) continue;
                            if (s.getContinuousWorkTime(timeStaffNumList, i) > 2 && !s.isMiddle(timeStaffNumList, i, unit)) {
                                unit.remove(s);
                                next = true;
                                last = s;
                                break;
                            }
                        }
                        if (next) continue;
                        //对连续工作时间为两小时的员工进行处理，整体工作时间后移半小时
                        Staff inconformity = unit.staffs.get(unit.staffs.size() - 1);
                        if (inconformity.getContinuousWorkTime(timeStaffNumList, i) <= 2) {
                            if (!inconformity.isLast(timeStaffNumList, i, unit)) {
                                unit.move(inconformity, timeStaffNumList, i);
                                if (unit.contains(inconformity)) unit.replace(last, inconformity, timeStaffNumList, i);
                                else last = inconformity;
                            } else {
                                unit.remove(inconformity);
                                last = inconformity;
                            }
                        }
                        //对连续工作时间大于两小时的员工，直接移除该时间工作
                        else if (inconformity.getContinuousWorkTime(timeStaffNumList, i) > 2 && !inconformity.isMiddle(timeStaffNumList, i, unit)) {
                            unit.remove(inconformity);
                            last = inconformity;
                        }
                    }
                    //处理工作时长不足两小时的员工
                    int j = i + 1;
                    for (int i1 = 0; i1 < unit.staffs.size(); i1++) {
                        Staff staff = unit.staffs.get(i1);
                        double time1 = staff.getContinuousWorkTime(timeStaffNumList, i);
                        if (time1 < 2) {
                            Staff minOne = staff.findNearMin(timeStaffNumList, i);
                            unit.replace(minOne, staff, timeStaffNumList, i);
                        }
                        //处理工作时长大于4小时的员工（要休息）
                        if (time1 > 4) {
                            boolean next = false;
                            for (j = i; j > -1; j--) {
                                TimeStaffNum tsm = timeStaffNumList.get(j);
                                for (int j1 = tsm.workUnits.size() - 1; j1 > -1; j1--) {
                                    if (!tsm.workUnits.get(j1).contains(staff)) {
                                        Staff newOne = staff.findNearMin(timeStaffNumList, i);//往前找，从前向后覆盖
                                        double time2 = newOne.getContinuousWorkTime(timeStaffNumList, j);
                                        if (time2 < 4)
                                            timeStaffNumList.get(j).workUnits.get(j1).replace(newOne, staff, timeStaffNumList, j, (int) ((time1 - 4) * 2));
                                        else
                                            timeStaffNumList.get(j).workUnits.get(j1).swap(newOne, staff, timeStaffNumList, j, j1, (int) time2);
                                        next = true;
                                        break;
                                    }
                                }
                                if (next) break;
                            }
                            if (j < 0 && !next) {
                                for (int j2 = i; j2 < timeStaffNumList.size(); j2++) {
                                    TimeStaffNum tsm = timeStaffNumList.get(j2);
                                    for (int j1 = 0; j1 < tsm.workUnits.size(); j1++) {
                                        if (!tsm.workUnits.get(j1).contains(staff)) {
                                            Staff newOne = staff.findNearMin(timeStaffNumList, -i);
                                            double time2 = newOne.getContinuousWorkTime(timeStaffNumList, j2);
                                            if (time2 < 4)
                                                timeStaffNumList.get(j2).workUnits.get(j1).replace(newOne, staff, timeStaffNumList, j2, (int) ((time1 - 4) * 2));
                                            else
                                                timeStaffNumList.get(j2).workUnits.get(j1).swap(staff, newOne, timeStaffNumList, j2, j1, (int) (time2));
                                            next = true;
                                            break;
                                        }
                                    }
                                    if (next) break;
                                }
                            }
                        }
                    }
                    nextI = j - 1;
                    if (nextI < 0) nextI = 0;
                }
                i = nextI;
                t++;
            }
        }catch (Exception e){
            System.out.println("优化失败");
        }
        try {
            timeStaffNumList = check(timeStaffNumList,true,0,0);
            //timeStaffNumList = check(timeStaffNumList);
            for(TimeStaffNum timeStaffNum:timeStaffNumList){
                for(TimeStaffNum.WorkUnit workUnit:timeStaffNum.workUnits){
                    for(Staff staff:workUnit.staffs){
                        if(staff.id< 0L) staff.setId(0);
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e);
        }
        for(Staff staff:staffList) staff.setContinuousWorkDay(timeStaffNumList);
        int size1=timeStaffNumList.size();
        int size2=timeStaffNumList.get(size1-1).workUnits.size();
        System.out.println("完成本次排班,星期为星期"+dayOfWeek);
        System.out.println("首班开始时间:"+timeStaffNumList.get(0).startTime);
        System.out.println("员工数:"+timeStaffNumList.get(0).workUnits.get(0).currentNum);
        System.out.println("末班开始时间:"+timeStaffNumList.get(size1-1).workUnits.get(size2-1).beginTime);
        System.out.println("员工数:"+timeStaffNumList.get(size1-1).workUnits.get(size2-1).currentNum);
        System.out.println("------------------------------------------------");
        return timeStaffNumList;
    }
    public ArrayList<Employee> transTo(List<EmployeeVO> employeeVOs){
        ArrayList<Employee> employees=new ArrayList<>();
        for(EmployeeVO employeeVO:employeeVOs)
            if(employeeVO!=null)
                employees.add(new Employee(employeeVO.getId(),employeeVO.getUid(),employeeVO.getPosition(),employeeVO.getShop(),employeeVO.getSalary(),employeeVO.getTime()));
        return employees;
    }
    public ArrayList<Long> transBack(LinkedList<Staff> staffs){
        ArrayList<Long> employees=new ArrayList<>();
        for(Staff staff:staffs) employees.add(staff.id);
        return employees;
    }
    public ArrayList<ArrayList<TimeStaffNum>> scheduleToTimestaffnum(Schedule.WorkUnit[][] week){
        ArrayList<ArrayList<TimeStaffNum>> timeStaffNumArrayList = new ArrayList<>();
        for(Schedule.WorkUnit[] day:week) {
            ArrayList<TimeStaffNum> timeStaffNumList = new ArrayList<>();
            for (int i = 0; i < day.length; i += 4) {
                if (i + 4 < day.length)
                    timeStaffNumList.add(new TimeStaffNum(Arrays.stream(day).toList().subList(i, i + 4)));
                else timeStaffNumList.add(new TimeStaffNum(Arrays.stream(day).toList().subList(i, day.length)));
            }
            timeStaffNumArrayList.add(timeStaffNumList);
        }
        return timeStaffNumArrayList;
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
                int halfHourNum=timeStaffNumList.get(7*i+j).size()*unitNum-unitNum+timeStaffNumList.get(7*i+j).get(timeStaffNumList.get(7*i+j).size()-1).workUnits.size();
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
    public List<Staff> getSelected(ArrayList<TimeStaffNum> timeStaffNums){
        List<Staff> staffs=new ArrayList<>();
        for(TimeStaffNum timeStaffNum:timeStaffNums){
            for(TimeStaffNum.WorkUnit unit:timeStaffNum.workUnits){
                for(Staff staff:unit.staffs){
                    if(!staffs.contains(staff)) staffs.add(staff);
                }
            }
        }
        return staffs;
    }
    //关于参数：weekNum：在该组排班中，该周是第几周；dayNum：在该周中，这是第几天，halfHourNum：在该天中，第几个半小时
    //关于输出结果：结果为所有符合要求的员工的id，按照匹配度排列
    public LinkedList<Long> getSuitableEmployees(long scheduleId,int weekNum,int dayNum,int halfHourNum){
        Schedule schedule= scheduleMapper.selectById(scheduleId);
        long shopId=schedule.getShop();
        ArrayList<Schedule.Week> weeks=schedule.getWeeks();
        Schedule.Week week1 = JSON.parseObject(JSON.toJSONString(schedule.getWeeks().get(weekNum)), Schedule.Week.class);
        Schedule.WorkUnit[][] week=week1.getData();
        List<EmployeeVO> employeeVoList = (List<EmployeeVO>) employeeService.getEmployeeByShop(shopId).getData();
        List<Employee> employees = transTo(employeeVoList);
        staffList=new Staff().toStaff(employees);

        ArrayList<ArrayList<TimeStaffNum>> days = scheduleToTimestaffnum(week);
        countWorkTime(days);
        ArrayList<TimeStaffNum> day=days.get(dayNum);
        int dayOfWeek=getDayOfWeek(day.get(0).startTime);
        List<Staff> selected=getSelected(day);
        staffList.removeAll(selected);

        matchingDegree.clear();
        TimeStaffNum timeStaffNum=day.get(halfHourNum/4);
        String start = timeStaffNum.startTime.toString();
        String regEx = "\\d+\\d+:+\\d+\\d+:+\\d+\\d";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(start);
        if(!m.find()) System.out.println("not match");
        start = m.group().substring(0, 5);
        String end = getEndTime(start);
        for (Staff staff : staffList) {
            staff.prefer.mateTime(start,end,dayOfWeek,staff);
        }
        List<Staff> keySetList= new ArrayList<>(matchingDegree.keySet());
        keySetList.sort((a,b)-> (int)(matchingDegree.get(b)*100-matchingDegree.get(a)*100));
        LinkedList<Long> employeeList=new LinkedList<>();
        for(Staff staff:keySetList){
            employeeList.add(staff.id);
        }
        staffList=null;
        return employeeList;
    }
}