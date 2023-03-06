package com.example.backend.Service.Impl;

import com.example.backend.POJO.Notification;
import com.example.backend.Service.NotificationService;
import com.example.backend.VO.ResultVO;
import com.example.backend.mapper.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Boolean createNotification(int type, String text, long from, long to) {
        Notification notification = new Notification(null, false, from, to, type, text, null);
        try {
            notificationMapper.insert(notification);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public ResultVO<Object> getNotificationList(Long userId) {
        return null;
    }

    @Override
    public ResultVO<Object> getUnreadNotificationList(Long userId) {
        return null;
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
