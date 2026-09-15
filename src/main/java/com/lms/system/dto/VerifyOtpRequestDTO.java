package com.lms.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "OTP is required")
    private String otp;
}