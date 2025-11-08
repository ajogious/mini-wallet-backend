package com.miniwallet.dto;

import com.miniwallet.model.VerificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BVNVerificationResponse {
    // Getters and Setters
    private boolean success;
    private String message;
    private VerificationStatus verificationStatus;
    private BigDecimal transactionLimit;

    // Constructors
    public BVNVerificationResponse() {}

    public BVNVerificationResponse(boolean success, String message, VerificationStatus verificationStatus, BigDecimal transactionLimit) {
        this.success = success;
        this.message = message;
        this.verificationStatus = verificationStatus;
        this.transactionLimit = transactionLimit;
    }

}