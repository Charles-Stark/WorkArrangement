package com.example.backend.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private Long id;
    private Boolean isRead;
    private Long fromUser;
    private Long toUser;
    private int type;
    private String text;
    private Date createAt;

}
