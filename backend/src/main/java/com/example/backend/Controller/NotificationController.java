package com.example.backend.Controller;

import com.example.backend.Service.NotificationService;
import com.example.backend.VO.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/all/{id}")
    public ResultVO<Object> getAllNotification(@PathVariable long id) {
        return notificationService.getNotificationList(id);
    }

    @GetMapping("/unread/{id}")
    public ResultVO<Object> getUnreadNotification(@PathVariable long id) {
        return notificationService.getUnreadNotificationList(id);
    }

    @GetMapping("/{id}")
    public ResultVO<Object> getNotification(@PathVariable long id) {
        return notificationService.getNotification(id);
    }

    @PostMapping("/read")
    public ResultVO<Object> setNotificationRead(@RequestParam("id") long id) {
        return notificationService.setNotificationRead(id);
    }

    @PostMapping("/read/all")
    public ResultVO<Object> setAllNotificationRead(@RequestParam("id") long id) {
        return notificationService.setAllNotificationRead(id);
    }

}
