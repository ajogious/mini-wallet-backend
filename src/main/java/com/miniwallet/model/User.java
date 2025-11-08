package com.miniwallet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID DEFAULT gen_random_uuid()", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String otherName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phoneNumber; // +2348012345678 format

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;

    @Column
    private String bvn; // Encrypted

    @Column
    private String nin; // Encrypted

    @Column
    private String virtualAccountNumber;

    @Column
    private String bankName;

    @Column(precision = 19, scale = 2)
    private BigDecimal transactionLimit; // ₦5,000,000 for verified

    @Column(nullable = false)
    private boolean twoFactorEnabled;

    @Column
    private String otpSecret;

    @Column
    private LocalDateTime otpExpiry;

    // KYC documents storage
    @Column
    private String idFrontImageUrl;

    @Column
    private String idBackImageUrl;

    @Column
    private String selfieImageUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public User(String firstName, String lastName, String otherName, String email, String phoneNumber, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherName = otherName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.verificationStatus = VerificationStatus.PENDING;
        this.transactionLimit = BigDecimal.ZERO;
        this.twoFactorEnabled = true;
    }
}
