package com.lms.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode, String purposeLabel) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your OTP Code - " + purposeLabel);
        message.setText("Your OTP code is: " + otpCode + "\n\nThis code will expire in 10 minutes.\n\n"
                + "If you did not request this, please ignore this email.");

        mailSender.send(message);
    }
}