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
    private RuleService ruleService;
    @Autowired
    private PreferenceService preferenceService;
    @Autowired
    private ScheduleMapper scheduleMapper;
    private final int unitNum=4;//一个为半小时
    private double maxServiceNumber=3.8;
    private int numberOnDuty=1;
    private List<Prefer> preference;
    private List<Staff> staffList;
    private Map<Staff,Float> matchingDegree;

    public void changeRule(Rule rule){
        if (rule==null) return;
        if(rule.getPrepareTime()==null) return;
        double prepareTime = rule.getPrepareTime();
        if(rule.getClosingTime()==null) return;
        double closingTime = rule.getClosingTime();
        if(rule.getNumberOnDuty()==null) return;
        this.numberOnDuty=rule.getNumberOnDuty();
        if(rule.getMaxServiceNumber()==null) return;
        this.maxServiceNumber=rule.getMaxServiceNumber();
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
        else if(index>0) return timeStaffNums.get(index-1).contains(staff);
        return false;
    }
    public class TimeStaffNum {     //记录时间段中员工数量
        private int minStaffNum;   // 最少员工数
        private int week;
        //private int time;      //时间段,数据类型暂定
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
                workUnits.add(new TimeStaffNum.WorkUnit(begin,new LinkedList<>(),flowUnits.get(i).getFlow()/maxServiceNumber));
            }
        }
        @Data
        public class WorkUnit{
            private Date beginTime;
            private LinkedList<Staff> staffs;
            private int minStaffNum;
            private int currentNum;
            public WorkUnit(Date beginTime, LinkedList<Staff> staffs, double min){
                this.beginTime=beginTime;
                this.staffs=staffs;
                minStaffNum=(int)min;
                if(minStaffNum==0) minStaffNum=numberOnDuty;
                currentNum=0;
            }
            public void add(Staff s){
                if(s==null) return;
                currentNum++;
                staffs.add(s);
                s.setDayWorkTime(0.5);
            }
            public void add(int index, Staff s){
                if(s==null) return;
                currentNum++;
                staffs.add(0,s);
                s.setDayWorkTime(0.5);
            }
            public void remove(Staff s){
                if(s==null) return;
                currentNum--;
                staffs.remove(s);
                s.setDayWorkTime(-0.5);
            }
            public boolean contains(Staff staff){
                return staffs.contains(staff);
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
                this.staffs.remove(staff);
                this.currentNum--;
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
        public void add(Staff s){
            currentStaffNum+=1;
            staff.add(s);
            for(TimeStaffNum.WorkUnit workUnit:workUnits) workUnit.add(s);
        }
        public boolean isFull(){
            return minStaffNum <= currentStaffNum;
        }
        public boolean contains(Staff staff){
            boolean result=false;
            for(TimeStaffNum.WorkUnit unit:workUnits)
                if(result=unit.contains(staff))
                    break;
            return result;
        }
    }
    private class Staff{       //方便使用，可换成employee
        private double dayWorkTime,weekWorkTime,continuousWorkTime;
        private Long id;
        private Prefer prefer;
        private int visitTime;
        public Staff(Long id){
            this.id=id;
            dayWorkTime=0;
            weekWorkTime=0;
            continuousWorkTime=0;
            visitTime=0;
        }
        public Staff(Employee e){
            this(e.getId());
            Preference preference=(Preference) preferenceService.getPreference(this.id).getData();
            prefer=new Prefer(preference);
            if(prefer.durationOfShift==null) prefer.durationOfShift=8;
            if(prefer.durationOfWeek==null) prefer.durationOfWeek=8;
        }
        public Staff(){}
        public boolean isTired(){
            if(visitTime==3){
                continuousWorkTime-=2;
                visitTime=0;
            }
            if(weekWorkTime>=prefer.durationOfWeek) return true;
            if(dayWorkTime>=prefer.durationOfShift) return true;
            if(continuousWorkTime>=4){
                visitTime++;
                return true;
            }
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
        private Staff findNearMin(List<TimeStaffNum> timeStaffNumList, int index){
            Staff min=new Staff();
            min.setDayWorkTime(40);
            if(index>0) {
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
            }
            else{
                for(int i=-index+1;i<timeStaffNumList.size();i++){
                    List<TimeStaffNum.WorkUnit> units = timeStaffNumList.get(i).workUnits;
                    for(int j=0;j<units.size();j++){
                        if(!units.get(j).contains(this))
                            for(Staff s:units.get(j).staffs){
                                if(s.dayWorkTime<min.dayWorkTime) min=s;
                                else if(s.dayWorkTime==min.dayWorkTime&&s.weekWorkTime<min.weekWorkTime) min=s;
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
            int start=0,end=6;
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
            visitTime=0;
            return continuousWorkTime;
        }
    }
    private class Prefer{
        private Long id;
        private List<Integer> workingDay;
        private List<Float> workingHours;
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
                addWorkingHours(s);
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
        public void addWorkingHours(String s){
            String[] list = s.split(",");
            workingHours = new ArrayList<>();
            int start,end;
            for(String s1:list) {
                start = Integer.parseInt(s1.substring(0, 2));
                end = Integer.parseInt(s1.substring(6, 8));
                if (s1.charAt(3) == '3') start += 0.5;
                if (s1.charAt(9) == '3') end += 0.5;
                for(float i=start;i<=end;i+=0.5){
                    if(!workingHours.contains(i)) workingHours.add(i);
                }
            }
            workingHours.sort((a,b)-> (int) (a+a-b-b));
        }
        public boolean mateDay(int week){
            if(workingDay==null) return true;
            for(int a:workingDay)
                if(a==week)
                    return true;
            return false;
        }
        public boolean mateTime(String start,String end){
            if(this.workingHours==null) {
                matchingDegree.put(findStaff(id),0.5f);
                return true;
            }
            if(findStaff(id)==null) return false;
            float startTime=Integer.parseInt(start.substring(0,2));
            if(start.charAt(3) == '3') startTime+=0.5;
            float endTime=Integer.parseInt(end.substring(0,2));
            if(end.charAt(3) == '3') endTime+=0.5;
            int k=-1;
            for(int i=0;i<workingHours.size();i++){
                float match;
                if(workingHours.get(i)==startTime){
                    if(workingHours.size()-i<=unitNum) {
                        match=(workingHours.size() - i) / (float) unitNum;
                        if(match>0) matchingDegree.put(findStaff(id), match);
                        return true;
                    }
                    else k=i;
                }
                if(k!=-1&&endTime<workingHours.get(i)) {
                    match=(i - k) / (float) unitNum;
                    if(match>0) matchingDegree.put(findStaff(id), match);
                    return true;
                }
            }
            return false;
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
    public List<ArrayList<TimeStaffNum>> arrangeWeek(long shopId, List<Flow> flowsOfWeek, long ruleId) throws RuntimeException{
        ArrayList<ArrayList<TimeStaffNum>> timeStaffNumList=new ArrayList<>();
        Rule rule = (Rule) ruleService.getRule(ruleId).getData();
        changeRule(rule);
        List<EmployeeVO> employeeVoList = (List<EmployeeVO>) employeeService.getEmployeeByShop(shopId).getData();
        List<Employee> employeeList = transTo(employeeVoList);
        if(employeeList ==null) return null;
        List<Preference> preferenceList = findPreference(employeeList);
        preference=new Prefer().toPrefer(preferenceList);
        staffList=new Staff().toStaff(employeeList);
        System.out.println("开始排班，本周排班起始星期为星期"+getDayOfWeek(flowsOfWeek.get(0).getDate()));
        for(Flow flow:flowsOfWeek) {
            timeStaffNumList.add((ArrayList<TimeStaffNum>) newArrange(flow));
        }
        return timeStaffNumList;
    }
    //完全按照所给的客流量排班
    public List<TimeStaffNum> newArrange(Flow flow) throws RuntimeException{
        int dayOfWeek=getDayOfWeek(flow.getDate());
        for(Staff staff:staffList) staff.dayWorkTime=0;
        List<TimeStaffNum> timeStaffNumList=new ArrayList<>();
        for(int i=0;i<flow.getFlowUnits().size();i+=unitNum){
            timeStaffNumList.add(new TimeStaffNum(flow.getFlowUnits(),i,atLeastNum(flow,i),unitNum));
        }
        int index=0;

        //准备加入循环
        int t=0,last1= timeStaffNumList.size()-1;
        matchingDegree=new HashMap<>();
        while(!timeStaffNumList.get(last1).isFull()) {
            if(t>1000) throw new RuntimeException("排班超时,搜索次数t="+t+",超时位置dayOfWeek="+dayOfWeek+",indexOfTimeList="+index);
            matchingDegree.clear();
            TimeStaffNum timeStaffNum=timeStaffNumList.get(index);
            for (Prefer prefer : preference) {
                String start = timeStaffNumList.get(0).startTime.toString();
                String regEx = "\\d+\\d+:+\\d+\\d+:+\\d+\\d";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(start);
                if(!m.find()) System.out.println("not match");
                start = m.group().substring(0, 5);
                String end = getEndTime(start);

                if (prefer.mateDay(dayOfWeek)) prefer.mateTime(start, end);
                if (timeStaffNum.isFull()) break;
            }
            if(t%2==1)
                for(Staff staff:staffList) staff.getContinuousWorkTime(timeStaffNumList,index);
            List<Staff> keySetList= new ArrayList<>(matchingDegree.keySet());
            keySetList.sort((a,b)-> (int)(matchingDegree.get(b)*100-matchingDegree.get(a)*100));
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
                        if(staff!=null&&!hasSelected(staff,timeStaffNumList,index)&&!staff.isTired()) timeStaffNum.add(staff);
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
                                    Staff newOne=staff.findNearMin(timeStaffNumList,i);
                                    timeStaffNumList.get(j).workUnits.get(j1).replace(newOne,staff,timeStaffNumList,j,(int)((time1-4)*2));
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
                                        timeStaffNumList.get(j2).workUnits.get(j1).replace(newOne,staff,timeStaffNumList, j2,(int)((time1-4)*2));
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
        System.out.println("新的排班数据以创建,id="+schedule.getId());
        return schedule.getId();
    }

}
