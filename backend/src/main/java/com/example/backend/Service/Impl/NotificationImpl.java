package com.example.backend.Service.Impl;

import com.example.backend.POJO.Notification;
import com.example.backend.Service.NotificationService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    private Boolean createNotification(int type, String text, long from, long to) {
        Notification notification = new Notification(null, false, from, to, type, text, null);
        try {
            notificationMapper.insert(notification);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public Boolean notifyWhenNewScheduleCreated(long newScheduleId, long from, long to) {
        return createNotification(1, String.valueOf(newScheduleId), from, to);
    }

    @Override
    public Boolean notifyWhenScheduleChanged(long changedScheduleId, long from, long to) {
        return createNotification(2, String.valueOf(changedScheduleId), from, to);
    }

    private void sortNotificationListByTimeOrder(List<Notification> notifications) {
        Comparator<Notification> notificationComparator = (o1, o2) -> o2.getCreateAt().compareTo(o1.getCreateAt());
        notifications.sort(notificationComparator);
    }

    @Override
    public ResultVO<Object> getNotificationList(Long userId) {
        Map<String, Object> searchingMap = new HashMap<>();
        searchingMap.put("toUser", userId);
        try {
            List<Notification> notifications = notificationMapper.selectByMap(searchingMap);
            sortNotificationListByTimeOrder(notifications);
            return new ResultVO<>(0, "获取通知成功", notifications);
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
            sortNotificationListByTimeOrder(notifications);
            return new ResultVO<>(0, "获取未读通知成功", notifications);
        } catch (Exception e) {
            return new ResultVO<>(-1, "获取未读通知失败", null);
        }
    }

    @Override
    public ResultVO<Object> getNotification(Long id) {
        try {
            return new ResultVO<>(0, "获取通知成功", notificationMapper.selectById(id));
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

}
