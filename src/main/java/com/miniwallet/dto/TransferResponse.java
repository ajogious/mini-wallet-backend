package com.miniwallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransferResponse {
    private boolean success;
    private String message;
    private UUID transactionId;
    private BigDecimal amount;
    private String recipientEmail;
    private BigDecimal senderNewBalance;

    // Constructors
    public TransferResponse(boolean success, String message, UUID transactionId,
            BigDecimal amount, String recipientEmail, BigDecimal senderNewBalance) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
        this.amount = amount;
        this.recipientEmail = recipientEmail;
        this.senderNewBalance = senderNewBalance;
    }

}