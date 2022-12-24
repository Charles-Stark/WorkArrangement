package com.example.backend.Service;

public interface MailService {

    void sendSimpleMail(String to, String subject, String content);
    void sendVerifyCode(String to, String code);

}
