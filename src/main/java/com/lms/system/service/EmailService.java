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

        System.out.println("========== RESEND DEBUG ==========");
        System.out.println("API key present: "
                + (resendApiKey != null && !resendApiKey.isBlank()));
        System.out.println("Original recipient: " + toEmail);

        Resend resend = new Resend(resendApiKey);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("LMS <onboarding@resend.dev>")
                .to("delivered@resend.dev")
                .subject("LMS OTP Test - " + purposeLabel)
                .text(
                    "Your OTP code is: " + otpCode
                    + "\n\nThis code will expire in 10 minutes."
                )
                .build();

        try {

            resend.emails().send(params);

            System.out.println("========== RESEND SUCCESS ==========");

        } catch (Exception e) {

            System.err.println("========== RESEND ERROR ==========");
            System.err.println("Error type: " + e.getClass().getName());
            System.err.println("Error message: " + e.getMessage());
            e.printStackTrace();
            System.err.println("===================================");

            throw new RuntimeException(
                    "Failed to send OTP email: " + e.getMessage(), e
            );
        }
    }
}