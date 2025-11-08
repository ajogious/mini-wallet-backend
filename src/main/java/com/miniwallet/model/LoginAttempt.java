package com.miniwallet.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "login_attempts")
public class LoginAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier; // email or phone

    @Column(nullable = false)
    private String otp;

    @Column(nullable = false)
    private LocalDateTime expiry;

    @Column(nullable = false)
    private boolean used = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public LoginAttempt() {}

    public LoginAttempt(String identifier, String otp, LocalDateTime expiry) {
        this.identifier = identifier;
        this.otp = otp;
        this.expiry = expiry;
        this.used = false;
    }

    // Utility method to check if OTP is valid
    public boolean isValid() {
        return !used && LocalDateTime.now().isBefore(expiry);
    }

}