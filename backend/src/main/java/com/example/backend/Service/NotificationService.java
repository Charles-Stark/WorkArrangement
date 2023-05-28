package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface NotificationService {

    void notifyWhenNewScheduleCreated(long newScheduleId, long from, long to);

    void notifyWhenScheduleChanged(long changedScheduleId, long from, long to);

    void notifyWhenOpenShift(long scheduleId, long from, long to);

    void notifyWhenAbsenceCreated(long absenceId, long from, long to);

    void notifyWhenAbsenceChecked(long absenceId, long from, long to);

    ResultVO<Object> getNotificationList(Long userId, int count);

    ResultVO<Object> getUnreadNotificationList(Long userId);

    ResultVO<Object> getNotification(Long id);

    ResultVO<Object> setNotificationRead(Long id);

    ResultVO<Object> setAllNotificationRead(Long userId);

    ResultVO<Object> countUnreadNotification(long userId);

    ResultVO<Object> deleteNotification(long id);

}
