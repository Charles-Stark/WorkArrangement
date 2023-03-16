package com.example.backend.Service.Impl;

import com.example.backend.POJO.Absence;
import com.example.backend.Service.AbsenceService;
import com.example.backend.Service.NotificationService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.AbsenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AbsenceServiceImpl implements AbsenceService {

    @Autowired
    private AbsenceMapper absenceMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ResultVO<Object> createAbsence(long employee, long manager, long shop, String reason, Date absenceDate, byte[] attachmentPhoto, String photoType) {
        Absence absence = new Absence(null, employee, manager, shop, reason, attachmentPhoto, photoType, null, absenceDate, null);
        try {
            absenceMapper.insert(absence);
            notificationService.notifyWhenAbsenceCreated(absence.getId(), employee, manager);
            return new ResultVO<>(0, "申请提交成功", absenceMapper.selectById(absence.getId()));
        } catch (Exception e) {
            return new ResultVO<>(-1, "申请提交失败", null);
        }
    }

    @Override
    public ResultVO<Object> getAbsence(long id) {
        try {
            return new ResultVO<>(0, "获取请假信息成功", absenceMapper.selectById(id));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取请假信息失败", null);
        }
    }

    private void sortAbsenceListByTimeOrder(List<Absence> absences) {
        Comparator<Absence> absenceComparator = (o1, o2) -> o2.getCreateAt().compareTo(o1.getCreateAt());
        absences.sort(absenceComparator);
    }

    @Override
    public ResultVO<Object> getAbsenceListByManager(long id) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("managerId", id);
        try {
            List<Absence> absences = absenceMapper.selectByMap(searchingMap);
            sortAbsenceListByTimeOrder(absences);
            return new ResultVO<>(0, "获取请假列表成功", absences);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取请假列表失败", null);
        }
    }

    @Override
    public ResultVO<Object> approveAbsenceOrNot(long id, boolean isApproved) {
        Absence absence = new Absence(id, null, null, null, null, null, null, isApproved, null, null);
        try {
            absenceMapper.updateById(absence);
            absence = absenceMapper.selectById(id);
            notificationService.notifyWhenAbsenceChecked(id, absence.getManagerId(), absence.getEmployeeId());
            // TODO update schedule
            return new ResultVO<>(0, "修改成功", null);
        } catch (Exception e) {
            return new ResultVO<>(-1, "修改失败", null);
        }
    }
}
