package com.lms.system.controller;

import com.lms.system.dto.*;
import com.lms.system.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        AuthResponseDTO response = authService.register(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO requestDTO) {
        AuthResponseDTO response = authService.login(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<AuthResponseDTO> verifyEmail(@Valid @RequestBody VerifyOtpRequestDTO requestDTO) {
        AuthResponseDTO response = authService.verifyEmail(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO requestDTO) {
        authService.forgotPassword(requestDTO);
        return new ResponseEntity<>("OTP sent to your registered email", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        authService.resetPassword(requestDTO);
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }
}