package com.miniwallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.miniwallet.model.VerificationStatus;
import lombok.*;

@Data
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String otherName;
    private String email;
    private String phoneNumber;
    private VerificationStatus verificationStatus;
    private BigDecimal transactionLimit;
    private String virtualAccountNumber;
    private String bankName;

    public UserResponse(UUID id, String firstName, String lastName, String otherName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherName = otherName;
        this.email = email;
    }

    public UserResponse(UUID id, String firstName, String lastName, String email, String phoneNumber,
                        VerificationStatus verificationStatus, BigDecimal transactionLimit,
                        String virtualAccountNumber, String bankName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.verificationStatus = verificationStatus;
        this.transactionLimit = transactionLimit;
        this.virtualAccountNumber = virtualAccountNumber;
        this.bankName = bankName;
    }
}
