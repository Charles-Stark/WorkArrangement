package com.example.backend.Service;

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
    RuleService ruleService;
    @Autowired
    PreferenceService preferenceService;
    @Autowired
    ScheduleMapper scheduleMapper;
    int unitNum=4;//一个为半小时
    double maxServiceNumber=3.8;
    int numberOnDuty=1;
    double prepareTime;
    double closingTime;
    List<EmployeeVO> employeeVoList;
    List<Employee> employeeList;
    List<Preference> preferenceList;
    List<Prefer> preference;
    List<Staff> staffList;
    Rule rule;
    private Staff findStaff(Long id,List<Staff> staffList){
        if(staffList.get(0).getClass()==Staff.class)
            for(Staff s:staffList)
                if(s.id==id)
                    return s;
        return null;
    }
    public class TimeStaffNum {     //记录时间段中员工数量
        private int minStaffNum;   // 最少员工数
        private int week;
        //private int time;      //时间段,数据类型暂定
        private Date startTime;
        private int currentStaffNum;     //目前员工人数
        private ArrayList<Staff> staff;
        private ArrayList<WorkUnit> workUnits;
        public TimeStaffNum(ArrayList<Flow.FlowUnit> flowUnits,int index, int minStaffNum,int unitNum) {
            startTime=flowUnits.get(index).getBeginAt();
            this.minStaffNum = minStaffNum;
            if(minStaffNum==0) this.minStaffNum=numberOnDuty;
            currentStaffNum = 0;
            staff=new ArrayList<Staff>();
            workUnits=new ArrayList<>();
            for(int i=index;i<index+unitNum&&i<flowUnits.size();i++){
                Date begin=flowUnits.get(i).getBeginAt();
                workUnits.add(new WorkUnit(begin,new LinkedList<>(),flowUnits.get(i).getFlow()/maxServiceNumber));
            }
        }
        //Test
        public TimeStaffNum(List<Tool.Flowtest> flows, int index, int minStaffNum, int unitNum) {
            String startTime=flows.get(index).getBegin();
            Calendar calendar =Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(startTime.substring(0,2)));
            calendar.set(Calendar.MINUTE,Integer.parseInt(startTime.substring(3,5)));
            this.startTime = calendar.getTime();
            this.minStaffNum = minStaffNum;
            if(minStaffNum==0) this.minStaffNum=numberOnDuty;
            currentStaffNum = 0;
            staff=new ArrayList<Staff>();
            workUnits=new ArrayList<>();
            for(int i=index;i<index+unitNum&&i<flows.size();i++){
                calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(flows.get(i).getBegin().substring(0,2)));
                calendar.set(Calendar.MINUTE,Integer.parseInt(flows.get(i).getBegin().substring(3,5)));
                Date begin=calendar.getTime();
                workUnits.add(new WorkUnit(begin,new LinkedList<>(),flows.get(i).getFlow()/maxServiceNumber));
            }
        }
        @Data
        public class WorkUnit{
            private Date beginTime;
            private LinkedList<Staff> staffs;
            private int minStaffNum;
            private int currentNum;
            public WorkUnit(Date beginTime,LinkedList<Staff> staffs,double min){
                this.beginTime=beginTime;
                this.staffs=staffs;
                minStaffNum=(int)min;
                if(minStaffNum==0) minStaffNum=numberOnDuty;
                currentNum=0;
            }
            public void add(Staff s){
                currentNum++;
                staffs.add(s);
                s.setDayWorkTime(0.5);
            }
            public void add( int index,Staff s){
                currentNum++;
                staffs.add(0,s);
                s.setDayWorkTime(0.5);
            }
            public void remove(Staff s){
                currentNum--;
                staffs.remove(s);
                s.setDayWorkTime(-0.5);
            }
            public boolean contains(Staff staff){
                return staffs.contains(staff);
            }
            public void move(Staff staff,List<TimeStaffNum> timeStaffNumList,int index){
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
                this.staffs.remove(staff);
                this.currentNum--;
            }
            public void replace(Staff newOne,Staff oldOne,List<TimeStaffNum> timeStaffNumList,int index){
                for(int i=timeStaffNumList.get(index).workUnits.indexOf(this);i<timeStaffNumList.get(index).workUnits.size();i++){
                    timeStaffNumList.get(index).workUnits.get(i).remove(oldOne);
                    timeStaffNumList.get(index).workUnits.get(i).add(0,newOne);
                }
                for(int i=index+1;i<timeStaffNumList.size();i++){
                    for(WorkUnit unit:timeStaffNumList.get(i).workUnits){
                        if(!unit.contains(oldOne)) return;
                        unit.remove(oldOne);
                        unit.add(0,newOne);
                    }
                }
            }
            public void replace(Staff newOne,Staff oldOne,List<TimeStaffNum> timeStaffNumList,int index,int length){
                TimeStaffNum timeStaffNum=timeStaffNumList.get(index);
                int count=0;
                int indexOfUnit=timeStaffNum.workUnits.indexOf(this);
                if(indexOfUnit==timeStaffNum.workUnits.size()-1) {
                    timeStaffNum=timeStaffNumList.get(index+1);
                    for(WorkUnit unit:timeStaffNum.workUnits){
                        unit.remove(oldOne);
                        unit.add(0,newOne);
                        count++;
                        if(count==length) break;
                    }
                    if(length>count) timeStaffNum.workUnits.get(timeStaffNum.workUnits.size()-1).replace(newOne,oldOne,timeStaffNumList,index+1,count-length);
                }
                else {
                    ArrayList<WorkUnit> workUnits=timeStaffNum.workUnits;
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
        public void add(Staff s){
            currentStaffNum+=1;
            staff.add(s);
            for(WorkUnit workUnit:workUnits) workUnit.add(s);
        }
        public boolean isFull(){
            return minStaffNum <= currentStaffNum;
        }
        public boolean contains(Staff staff){
            boolean result=false;
            for(WorkUnit unit:workUnits)
                if(result=unit.contains(staff))
                    break;
            return result;
        }
    }
    private class Staff{       //方便使用，可换成employee
        private double dayWorkTime,weekWorkTime,continuousWorkTime;
        private Long id;
        private Prefer prefer;
        public Staff(Long id){
            this.id=id;
            dayWorkTime=0;
            weekWorkTime=0;
            continuousWorkTime=0;
        }
        public Staff(Employee e){
            this.id=e.getId();
            dayWorkTime=0;
            weekWorkTime=0;
            continuousWorkTime=0;
            Preference preference=(Preference) preferenceService.getPreference(this.id).getData();
            prefer=new Prefer(preference);
            if(prefer.durationOfShift==null) prefer.durationOfShift=8;
            if(prefer.durationOfWeek==null) prefer.durationOfWeek=8;
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
            continuousWorkTime+=time;
        }
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
        public boolean isLast(List<TimeStaffNum> timeStaffNumList, int indexOfList, TimeStaffNum.WorkUnit unit){
            int indexOfUnits=timeStaffNumList.get(indexOfList).workUnits.indexOf(unit);
            if(indexOfList==timeStaffNumList.size()-1&&indexOfUnits==unitNum-1) return true;
            if(indexOfUnits==unitNum-1){
                return !timeStaffNumList.get(indexOfList+1).workUnits.get(0).contains(this);
            }
            else return !timeStaffNumList.get(indexOfList).workUnits.get(indexOfUnits+1).contains(this);
        }
        private Staff findNearMin(List<TimeStaffNum> timeStaffNumList,int index){
            Staff min=new Staff();
            min.setDayWorkTime(40);
            for(int i=index-1;i>-1;i--){
                List<TimeStaffNum.WorkUnit> units = timeStaffNumList.get(i).workUnits;
                for(int j=units.size()-1;j>-1;j--){
                    if(!units.get(j).contains(this))
                        for(Staff s:units.get(j).staffs){
                            if(s.dayWorkTime<min.dayWorkTime) min=s;
                            else if(s.dayWorkTime==min.dayWorkTime&&s.weekWorkTime<min.weekWorkTime) min=s;
                        }
                }
                if(min.dayWorkTime!=40) break;
            }
            return min;
        }
        public List<Staff> toStaff(List<Employee> employees){
            List<Staff> staffs=new ArrayList<>();
            for(Employee employee:employees) staffs.add(new Staff(employee));
            return staffs;
        }
        public double getContinuousWorkTime(List<TimeStaffNum> timeStaffNumList,int index){
            int start=0,end=6;
            continuousWorkTime=0;
            double count=0;
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
    }
    private class Prefer{
        private Long id;
        private List<Integer> workingDay;
        private List<String> workingHours;
        private Integer durationOfShift;
        private Integer durationOfWeek;
        public Prefer(Preference preference){
            this.id=preference.getId();
            this.durationOfShift=preference.getDurationOfShift();
            this.durationOfWeek=preference.getDurationOfWeek();
            String s=preference.getWorkingDay();
            if(s!=null){
                String[] list=s.split(",");
                workingDay=new ArrayList<Integer>();
                for(String a:list) workingDay.add(Integer.parseInt(a));
            }
            s = preference.getWorkingHours();
            if(s!=null) {
                String[] list = s.split(",");
                workingHours = new ArrayList<String>();
                Collections.addAll(workingHours, list);
            }
        }
        public Prefer(){}
        public List<Prefer> toPrefer(List<Preference> preferenceList){
            var preferList=new ArrayList<Prefer>();
            preferenceList.forEach(a->{
                preferList.add(new Prefer(a));
            });
            return preferList;
        }
        public boolean mateDay(int week){
            if(workingDay==null) return true;
            for(int a:workingDay)
                if(a==week)
                    return true;
            return false;
        }
        public boolean mateTime(String start,String end){
            if(this.workingHours==null) return false;
            String regEx=start+"-"+end;
            Pattern pattern = Pattern.compile(regEx);
            for(String a:workingHours){
                Matcher matcher = pattern.matcher(a);
                if(matcher.find())
                    return true;
            }
            for(String a:workingHours){
                if (Integer.parseInt(start.substring(0,2))>=Integer.parseInt(a.substring(0,2))&&Integer.parseInt(end.substring(0,2))<=Integer.parseInt(a.substring(6,8)))
                    return true;
            }
            return false;
        }
    }
    private ArrayList<Preference> findPreference(List<Employee> list){
        var pList=new ArrayList<Preference>();
        list.forEach(e->{
            Preference preference=(Preference) preferenceService.getPreference(e.getId()).getData();

            pList.add((Preference) preferenceService.getPreference(e.getId()).getData());
        });
        return pList;
    }
    private int atLeastNum(Flow flow,int index){  //一个flow为半小时，暂定两小时为一组
        double max;
        max= flow.getFlowUnits().get(index).getFlow();
        if(index<=flow.getFlowUnits().size()-unitNum){
            for(int i=index;i<i+unitNum;i++){
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
    public List<ArrayList<TimeStaffNum>> arrangeWeek(long shopId, List<Flow> flowsOfWeek){
        ArrayList<ArrayList<TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        rule=(Rule)ruleService.getRuleByShop(shopId).getData();
        employeeVoList= (List<EmployeeVO>) employeeService.getEmployeeByShop(shopId).getData();
        employeeList=transTo(employeeVoList);
        if(employeeList==null) return null;
        preferenceList=findPreference(employeeList);
        preference=new Prefer().toPrefer(preferenceList);
        staffList=new Staff().toStaff(employeeList);
        for(Flow flow:flowsOfWeek)
            timeStaffNumList.add((ArrayList<TimeStaffNum>) newArrange(shopId,flow));
        return timeStaffNumList;
    }
    public List<TimeStaffNum> newArrange(long shopId,Flow flow){
        int dayOfWeek=getDayOfWeek(flow.getDate());

        List<TimeStaffNum> timeStaffNumList=new ArrayList<>();
        Schedule schedule;
        for(int i=0;i<flow.getFlowUnits().size();i+=unitNum){
            timeStaffNumList.add(new TimeStaffNum(flow.getFlowUnits(),i,atLeastNum(flow,i),unitNum));
        }
        List<Integer> index=new ArrayList<>();

        //准备加入循环
        for(int i=0;i<timeStaffNumList.size();i++) index.add(i);
        int t=0;
        while(t<timeStaffNumList.size()) {
            index.sort((a, b) -> {
                if (timeStaffNumList.get(a).isFull()) return 1;
                if (timeStaffNumList.get(b).isFull()) return -1;
                return timeStaffNumList.get(a).minStaffNum - timeStaffNumList.get(b).minStaffNum;
            });
            for (Prefer prefer : preference) {
                String start = timeStaffNumList.get(0).startTime.toString();
                String regEx = "\\d+\\d+:+\\d+\\d+:+\\d+\\d";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(start);
                start = m.group().substring(0, 5);
                String end = getEndTime(start);

                if (prefer.mateDay(dayOfWeek)&&prefer.mateTime(start, end)){
                    Staff staff=findStaff(prefer.id, staffList);
                    if(!staff.isTired()) timeStaffNumList.get(index.get(0)).add(staff);
                }
                if (timeStaffNumList.get(index.get(0)).isFull()) break;
            }
            if (!timeStaffNumList.get(index.get(0)).isFull()) {
                for (Prefer prefer : preference) {
                    if (prefer.workingHours == null) {
                        Staff staff=findStaff(prefer.id, staffList);
                        if(!staff.isTired()) timeStaffNumList.get(index.get(0)).add(staff);
                    }
                    if (timeStaffNumList.get(index.get(0)).isFull()) break;
                }
            }
            t++;
        }

        //优化
        //优化过程尽量只改动已经排上的人
        //在已有基础上进行时间上的增删改
        //优化过程将不考虑员工工作时间偏好
        Staff last=null;
        for(int i=0;i<timeStaffNumList.size();i++)
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
                    if(next){
                        next=false;
                        continue;
                    }
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
                for(Staff staff:unit.staffs){
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
                                    Staff newOne=staff.findNearMin(timeStaffNumList,i);
                                    timeStaffNumList.get(j).workUnits.get(j1).replace(newOne,staff,timeStaffNumList,j,(int)(time1-4)*2);
                                    next=true;
                                    break;
                                }
                            }
                            if(next) break;
                        }
                    }
                }
                i=j-1;
            }

        return timeStaffNumList;
    }
    //Test

    private int atLeastNum(List<Tool.Flowtest> flow, int index){  //一个flow为半小时，暂定两小时为一组
        double max;
        max= flow.get(index).getFlow();
        if(index<=flow.size()-unitNum){
            for(int i=index;i<index+unitNum;i++){
                if(flow.get(i).getFlow()>max) max=flow.get(i).getFlow();
            }
        }
        else {
            for(int i=index;i<flow.size();i++){
                if(flow.get(i).getFlow()>max) max=flow.get(i).getFlow();
            }
        }
        return (int)(max/maxServiceNumber);
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
    //一天
    public List<TimeStaffNum> newArrangeTest(long shopId, List<Tool.Flowtest> flow){

        //Rule rule=(Rule)ruleService.getRuleByShop(shopId).getData();
        int dayOfWeek=flow.get(0).getDayOfWeek();

        List<TimeStaffNum> timeStaffNumList=new ArrayList<>();
        Schedule schedule;
        for(int i=0;i<flow.size();i+=unitNum){
            timeStaffNumList.add(new TimeStaffNum(flow,i,atLeastNum(flow,i),unitNum));
        }
        List<Integer> index=new ArrayList<>();

        //获得初始排班信息
        for(int i=0;i<timeStaffNumList.size();i++) index.add(i);
        int t=0;
        while(t<timeStaffNumList.size()) {
            index.sort((a, b) -> {
                if (timeStaffNumList.get(a).isFull()) return 1;
                if (timeStaffNumList.get(b).isFull()) return -1;
                return timeStaffNumList.get(a).minStaffNum - timeStaffNumList.get(b).minStaffNum;
            });
            for (Prefer prefer : preference) {
                String start = timeStaffNumList.get(index.get(0)).startTime.toString();
                String regEx = "\\d+\\d+:+\\d+\\d+:+\\d+\\d";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(start);
                if(!m.find()) System.out.println("not match");
                start = m.group().substring(0, 5);
                String end = getEndTime(start);

                if (prefer.mateTime(start, end)&&prefer.mateDay(dayOfWeek)){
                    Staff staff=findStaff(prefer.id, staffList);
                    if(!staff.isTired()) timeStaffNumList.get(index.get(0)).add(staff);
                }
                if (timeStaffNumList.get(index.get(0)).isFull()) break;
            }
            if (!timeStaffNumList.get(index.get(0)).isFull()) {
                for (Prefer prefer : preference) {
                    if (prefer.workingHours == null) {
                        Staff staff=findStaff(prefer.id, staffList);
                        if(!staff.isTired()) timeStaffNumList.get(index.get(0)).add(staff);
                    }
                    if (timeStaffNumList.get(index.get(0)).isFull()) break;
                }
            }
            t++;
        }

        //优化
        //优化过程尽量只改动已经排上的人
        //在已有基础上进行时间上的增删改
        //优化过程将不考虑员工工作时间偏好
        Staff last=null;
        for(int i=0;i<timeStaffNumList.size();i++)
            for(int i1=0;i1<timeStaffNumList.get(i).workUnits.size();i1++){
                TimeStaffNum.WorkUnit unit = timeStaffNumList.get(i).workUnits.get(i1);
            //for(TimeStaffNum.WorkUnit unit:timeStaffNumList.get(i).workUnits){
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
                    if(next){
                        next=false;
                        continue;
                    }
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

                boolean out=false;
                int j=i+1;
                for(Staff staff:unit.staffs){
                    double time1=staff.getContinuousWorkTime(timeStaffNumList,i);
                    //处理工作时长不足两小时的员工
                    if(time1<2){
                        Staff minOne=staff.findNearMin(timeStaffNumList,i);
                        unit.replace(minOne,staff,timeStaffNumList,i);
                    }
                    //处理工作时长大于4小时的员工（要休息）
                    if(time1>4){
                        for(j=i;j>-1;j--){
                            TimeStaffNum tsm=timeStaffNumList.get(j);
                            for(int j1=tsm.workUnits.size()-1;j1>-1;j1--){
                                if(!tsm.workUnits.get(j1).contains(staff)){
                                    Staff newOne=staff.findNearMin(timeStaffNumList,i);
                                    timeStaffNumList.get(j).workUnits.get(j1).replace(newOne,staff,timeStaffNumList,j,(int)(time1-4)*2);
                                    out=true;
                                    break;
                                }
                            }
                            if(out) break;
                        }
                    }
                }
                i=j-1;
                if(out&&i<3) break;
            }

        return timeStaffNumList;
    }
    //入循环
    public ArrayList<ArrayList<TimeStaffNum>> arrangeWeekTest(long shopId, List<List<Tool.Flowtest>> flowsOfWeek){
        ArrayList<ArrayList<TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        employeeVoList= (List<EmployeeVO>) employeeService.getEmployeeByShop(shopId).getData();
        employeeList=transTo(employeeVoList);
        preferenceList=findPreference(employeeList);
        preference=new Prefer().toPrefer(preferenceList);
        staffList=new Staff().toStaff(employeeList);
        for(List<Tool.Flowtest> flows:flowsOfWeek)
            timeStaffNumList.add((ArrayList<TimeStaffNum>) newArrangeTest(shopId,flows));
        return timeStaffNumList;
    }

    public void outPut(List<List<TimeStaffNum>> timeStaffNumList,long shopId,long ruleId){
        ArrayList<Schedule.Week> weeks=new ArrayList<>();
        for(int i=0 ;i<(timeStaffNumList.size()-1)/7+1;i++){
            Schedule.Week week=new Schedule.Week();
            week.setStartAt(timeStaffNumList.get(i).get(0).startTime);
            if(i+6<timeStaffNumList.size()) week.setEndAt(timeStaffNumList.get(i+6).get(0).startTime);
            else week.setEndAt(timeStaffNumList.get(timeStaffNumList.size()-1).get(0).startTime);
            Schedule.WorkUnit[][] workUnits=new Schedule.WorkUnit[7][timeStaffNumList.get(0).size()*unitNum];
            for(int j=0;j<7;j++){
                if(timeStaffNumList.size() <j+1) break;
                int halfHourNum=timeStaffNumList.get(i+j).size()*unitNum-unitNum+timeStaffNumList.get(i+j).get(timeStaffNumList.get(i+j).size()-1).workUnits.size();
                for(int k=0;k<halfHourNum;k++){
                    workUnits[j][k]=new Schedule.WorkUnit();
                    workUnits[j][k].setBeginTime(timeStaffNumList.get(7*i+j).get(k/unitNum).startTime);
                    workUnits[j][k].setEmployees(transBack(timeStaffNumList.get(7*i+j).get(k/unitNum).workUnits.get(k%unitNum).staffs));
                }
            }
            week.setData(workUnits);
            weeks.add(week);
        }
        Schedule schedule=new Schedule(null,shopId, 1L,new Date(),true,ruleId,timeStaffNumList.get(0).get(0).startTime,timeStaffNumList.get(timeStaffNumList.size()-1).get(0).startTime,weeks);
        scheduleMapper.insert(schedule);
    }
}
