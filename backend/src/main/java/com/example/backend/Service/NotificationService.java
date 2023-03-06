package com.example.backend.Service;

import com.example.backend.VO.ResultVO;

public interface NotificationService {

    Boolean createNotification(int type, String text, long from, long to);

    ResultVO<Object> getNotificationList(Long userId);

    ResultVO<Object> getUnreadNotificationList(Long userId);

    ResultVO<Object> getNotification(Long id);

    ResultVO<Object> setNotificationRead(Long id);

    ResultVO<Object> setAllNotificationRead(Long userId);

}
