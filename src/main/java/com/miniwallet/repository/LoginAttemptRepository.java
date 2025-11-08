package com.miniwallet.repository;

import com.miniwallet.model.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    Optional<LoginAttempt> findByIdentifierAndOtpAndUsedFalse(String identifier, String otp);
    Optional<LoginAttempt> findTopByIdentifierOrderByCreatedAtDesc(String identifier);
    boolean existsByIdentifierAndUsedFalse(String identifier);
}
