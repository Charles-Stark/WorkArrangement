package com.example.backend.Service.Impl;

import com.example.backend.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String fromUser;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setFrom(fromUser);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(content);

        mailSender.send(simpleMailMessage);
    }

    @Override
    public void sendVerifyCode(String to, String code) {
        try {
            String subject = "【验证码】" + code;
            String content = "确认你的邮箱地址\n" + code + "\n验证码在10分钟后过期";
            sendSimpleMail(to, subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
