package com.mysql.sbb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    public void sendMail(String to, String subject, String content){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setTo(to);
        message.setText(content);
        message.setFrom("zxc8661@naver.com");
        this.javaMailSender.send(message);
    }
}
