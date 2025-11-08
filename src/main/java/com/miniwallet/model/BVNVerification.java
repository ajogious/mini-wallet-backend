package com.miniwallet.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bvn_verifications")
public class BVNVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bvn;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String otherName;

    @Column(nullable = false)
    private String dateOfBirth;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus status;

    @Column(columnDefinition = "TEXT")
    private String responseMessage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public BVNVerification() {}

    public BVNVerification(String bvn, String firstName, String lastName, String otherName, String dateOfBirth, String phoneNumber, User user) {
        this.bvn = bvn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherName = otherName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.user = user;
        this.status = VerificationStatus.PENDING;
    }


}