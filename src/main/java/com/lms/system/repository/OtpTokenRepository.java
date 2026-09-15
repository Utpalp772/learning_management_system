package com.lms.system.repository;

import com.lms.system.model.OtpToken;
import com.lms.system.model.OtpPurpose;
import com.lms.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByUserAndPurposeAndUsedFalseOrderByIdDesc(User user, OtpPurpose purpose);
    List<OtpToken> findByUser(User user);
}