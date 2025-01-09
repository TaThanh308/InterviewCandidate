package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject , String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("hanhhoang420@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("send mail success ....");}


    public void sendResetPasswordEmail(String toEmail, String token) {
        String subject = "Password Reset";
        String body = "We have just received a password reset request for: " + toEmail
                + "\nPlease click here to reset your password:\n" + "http://localhost:8088/reset-password?token=" + token
                + "\nFor your security, the link will expire in 24 hours or immediately after you reset your password.\n"
                + "Thanks & Regards!"
                + "IMS Team.”";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}

