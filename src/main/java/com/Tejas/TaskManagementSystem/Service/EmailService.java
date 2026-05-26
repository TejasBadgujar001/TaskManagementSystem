package com.Tejas.TaskManagementSystem.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
//Service responsible for sending email notifications
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromMail;
    public void sendEmail(String to,String subject, String body){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(subject);
            message.setTo(to);
            message.setText(body);
            message.setFrom(fromMail);
            mailSender.send(message);
        }catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }
    }
}
