package com.example.backend.VO;

import com.example.backend.POJO.Notification;
import com.example.backend.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class NotificationVO {

    @Autowired
    private UserMapper userMapper;
    private static NotificationVO notificationVO;

    @PostConstruct
    private void init() {
        notificationVO = this;
        notificationVO.userMapper = this.userMapper;
    }

    private Long id;
    private Boolean isRead;
    private Long fromUser;
    private Long toUser;
    private Integer type;
    private String text;
    private Date createAt;
    private String fromUsername;

    public NotificationVO(Notification notification) {
        id = notification.getId();
        isRead = notification.getIsRead();
        fromUser = notification.getFromUser();
        toUser = notification.getToUser();
        type = notification.getType();
        text = notification.getText();
        createAt = notification.getCreateAt();
        fromUsername = notificationVO.userMapper.selectById(id).getUsername();
    }

}
