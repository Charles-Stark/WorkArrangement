package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface NotificationService {

    Boolean notifyWhenNewScheduleCreated(long newScheduleId, long from, long to);

    Boolean notifyWhenScheduleChanged(long changedScheduleId, long from, long to);

    Boolean notifyWhenOpenShift(long scheduleId, long from, long to);

    Boolean notifyWhenAbsenceCreated(long absenceId, long from, long to);

    Boolean notifyWhenAbsenceChecked(long absenceId, long from, long to);

    ResultVO<Object> getNotificationList(Long userId, int count);

    ResultVO<Object> getUnreadNotificationList(Long userId);

    ResultVO<Object> getNotification(Long id);

    ResultVO<Object> setNotificationRead(Long id);

    ResultVO<Object> setAllNotificationRead(Long userId);

    ResultVO<Object> countUnreadNotification(long userId);

    ResultVO<Object> deleteNotification(long id);

}
