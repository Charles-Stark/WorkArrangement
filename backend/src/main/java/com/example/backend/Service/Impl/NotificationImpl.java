package com.example.backend.Service.Impl;

import com.example.backend.POJO.Notification;
import com.example.backend.Service.NotificationService;
import com.example.backend.VO.NotificationVO;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NotificationImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    private void createNotification(int type, String text, long from, long to) {
        Notification notification = new Notification(null, false, from, to, type, text, null);
        try {
            notificationMapper.insert(notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notifyWhenNewScheduleCreated(long newScheduleId, long from, long to) {
        createNotification(1, String.valueOf(newScheduleId), from, to);
    }

    @Override
    public void notifyWhenScheduleChanged(long changedScheduleId, long from, long to) {
        createNotification(2, String.valueOf(changedScheduleId), from, to);
    }

    @Override
    public void notifyWhenOpenShift(long scheduleId, long from, long to, long beginTime, long endTime) {
        // text字段格式："排班表id,开始时间戳,结束时间戳" (英文逗号隔开)
        createNotification(3, scheduleId + "," + beginTime + "," + endTime, from, to);
    }

    @Override
    public void notifyWhenAbsenceCreated(long absenceId, long from, long to) {
        createNotification(4, String.valueOf(absenceId), from, to);
    }

    @Override
    public void notifyWhenAbsenceChecked(long absenceId, long from, long to) {
        createNotification(5, String.valueOf(absenceId), from, to);
    }

    private void sortNotificationListByTimeOrder(List<NotificationVO> notificationVOs) {
        Comparator<NotificationVO> notificationVOComparator = (o1, o2) -> o2.getCreateAt().compareTo(o1.getCreateAt());
        notificationVOs.sort(notificationVOComparator);
    }

    @Override
    public ResultVO<Object> getNotificationList(Long userId, int count) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("toUser", userId);
        try {
            List<Notification> notifications = notificationMapper.selectByMap(searchingMap);

            List<NotificationVO> notificationVOs = new ArrayList<>();
            for (Notification notification : notifications) {
                notificationVOs.add(new NotificationVO(notification));
            }

            sortNotificationListByTimeOrder(notificationVOs);
            notificationVOs = count > 0 ? (count > notificationVOs.size() ? notificationVOs : notificationVOs.subList(0, count)) : notificationVOs;
            return new ResultVO<>(0, "获取通知成功", notificationVOs);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取通知失败", null);
        }
    }

    @Override
    public ResultVO<Object> getUnreadNotificationList(Long userId) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("toUser", userId);
        searchingMap.put("isRead", false);
        try {
            List<Notification> notifications = notificationMapper.selectByMap(searchingMap);

            List<NotificationVO> notificationVOs = new ArrayList<>();
            for (Notification notification : notifications) {
                notificationVOs.add(new NotificationVO(notification));
            }

            sortNotificationListByTimeOrder(notificationVOs);
            return new ResultVO<>(0, "获取未读通知成功", notificationVOs);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取未读通知失败", null);
        }
    }

    @Override
    public ResultVO<Object> getNotification(Long id) {
        try {
            return new ResultVO<>(0, "获取通知成功", new NotificationVO(notificationMapper.selectById(id)));
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取通知失败", null);
        }
    }

    @Override
    public ResultVO<Object> setNotificationRead(Long id) {
        Notification notification = new Notification(id, true, null, null, null, null, null);
        try {
            notificationMapper.updateById(notification);
        } catch (Exception e) {
            return new ResultVO<>(-1, "消息已读失败", null);
        }
        return new ResultVO<>(0, "消息已读成功", null);
    }

    @Override
    public ResultVO<Object> setAllNotificationRead(Long userId) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("toUser", userId);
        try {
            List<Notification> notifications = notificationMapper.selectByMap(searchingMap);
            for (Notification notification : notifications) {
                notification.setIsRead(true);
                notificationMapper.updateById(notification);
            }
        } catch (Exception e) {
            return new ResultVO<>(-1, "消息已读失败", null);
        }
        return new ResultVO<>(0, "消息已读成功", null);
    }

    @Override
    public ResultVO<Object> countUnreadNotification(long userId) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("toUser", userId);
        searchingMap.put("isRead", false);
        try {
            return new ResultVO<>(0, "获取未读消息数量成功", notificationMapper.selectByMap(searchingMap).size());
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取未读消息数量失败", null);
        }
    }

    @Override
    public ResultVO<Object> deleteNotification(long id) {
        try {
            notificationMapper.deleteById(id);
            return new ResultVO<>(0, "删除通知成功", null);
        } catch (Exception e) {
            return new ResultVO<>(-1, "删除通知失败", null);
        }
    }

}
