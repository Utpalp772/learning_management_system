package com.lms.system.service;

import com.lms.system.model.OtpPurpose;
import com.lms.system.model.OtpToken;
import com.lms.system.model.User;
import com.lms.system.repository.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpTokenRepository otpTokenRepository;

    @Autowired
    private EmailService emailService;

    private static final long OTP_VALID_MINUTES = 10;

    public void generateAndSendOtp(User user, OtpPurpose purpose) {
        String otpCode = generateSixDigitOtp();

        OtpToken otpToken = new OtpToken();
        otpToken.setOtpCode(otpCode);
        otpToken.setUser(user);
        otpToken.setPurpose(purpose);
        otpToken.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES));
        otpToken.setUsed(false);

        otpTokenRepository.save(otpToken);

        String label = (purpose == OtpPurpose.EMAIL_VERIFICATION) ? "Email Verification" : "Password Reset";
        emailService.sendOtpEmail(user.getEmail(), otpCode, label);
    }

    public boolean validateOtp(User user, String submittedOtp, OtpPurpose purpose) {
        Optional<OtpToken> latestOtp = otpTokenRepository
                .findTopByUserAndPurposeAndUsedFalseOrderByIdDesc(user, purpose);

        if (latestOtp.isEmpty()) {
            return false;
        }

        OtpToken otpToken = latestOtp.get();

        if (otpToken.isUsed()) {
            return false;
        }

        if (otpToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        if (!otpToken.getOtpCode().equals(submittedOtp)) {
            return false;
        }

        otpToken.setUsed(true);
        otpTokenRepository.save(otpToken);
        return true;
    }

    private String generateSixDigitOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}