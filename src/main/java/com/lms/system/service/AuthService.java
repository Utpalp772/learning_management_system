package com.lms.system.service;

import com.lms.system.dto.AuthResponseDTO;
import com.lms.system.dto.ForgotPasswordRequestDTO;
import com.lms.system.dto.LoginRequestDTO;
import com.lms.system.dto.RegisterRequestDTO;
import com.lms.system.dto.ResetPasswordRequestDTO;
import com.lms.system.dto.VerifyOtpRequestDTO;
import com.lms.system.model.OtpPurpose;
import com.lms.system.model.Role;
import com.lms.system.model.User;
import com.lms.system.repository.UserRepository;
import com.lms.system.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OtpService otpService;

    public AuthResponseDTO register(RegisterRequestDTO requestDTO) {

        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        if (userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Role role;

        try {
            role = Role.valueOf(requestDTO.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(
                    "Invalid role. Must be STUDENT, INSTRUCTOR, or ADMIN"
            );
        }

        User user = new User();

        user.setUsername(requestDTO.getUsername());
        user.setEmail(requestDTO.getEmail());
        user.setPassword(
                passwordEncoder.encode(requestDTO.getPassword())
        );
        user.setRole(role);
        user.setEmailVerified(false);

        userRepository.save(user);

        otpService.generateAndSendOtp(
                user,
                OtpPurpose.EMAIL_VERIFICATION
        );

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new AuthResponseDTO(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }

    public AuthResponseDTO login(LoginRequestDTO requestDTO) {

        User user = userRepository
                .findByUsername(requestDTO.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(
                requestDTO.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new AuthResponseDTO(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }

    public AuthResponseDTO verifyEmail(
            VerifyOtpRequestDTO requestDTO
    ) {

        User user = userRepository
                .findByUsername(requestDTO.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        boolean valid = otpService.validateOtp(
                user,
                requestDTO.getOtp(),
                OtpPurpose.EMAIL_VERIFICATION
        );

        if (!valid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        user.setEmailVerified(true);

        userRepository.save(user);

        String token = jwtUtil.generateToken(
                user.getUsername(),
                user.getRole().name()
        );

        return new AuthResponseDTO(
                token,
                user.getUsername(),
                user.getRole().name()
        );
    }

    public void forgotPassword(
            ForgotPasswordRequestDTO requestDTO
    ) {

        User user = userRepository
                .findByUsername(requestDTO.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        otpService.generateAndSendOtp(
                user,
                OtpPurpose.PASSWORD_RESET
        );
    }

    public void resetPassword(
            ResetPasswordRequestDTO requestDTO
    ) {

        User user = userRepository
                .findByUsername(requestDTO.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        boolean valid = otpService.validateOtp(
                user,
                requestDTO.getOtp(),
                OtpPurpose.PASSWORD_RESET
        );

        if (!valid) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        user.setPassword(
                passwordEncoder.encode(
                        requestDTO.getNewPassword()
                )
        );

        userRepository.save(user);
    }
}