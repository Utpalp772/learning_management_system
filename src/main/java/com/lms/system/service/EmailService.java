package com.lms.system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    public void sendOtpEmail(String toEmail, String otpCode, String purposeLabel) {

        Resend resend = new Resend(resendApiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("LMS <onboarding@resend.dev>")
                .to(toEmail)
                .subject("Your OTP Code - " + purposeLabel)
                .text(
                    "Your OTP code is: " + otpCode
                    + "\n\nThis code will expire in 10 minutes."
                    + "\n\nIf you did not request this, please ignore this email."
                )
                .build();

        try {
            resend.emails().send(params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}