package com.miniwallet.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class TransferResponse {
    private boolean success;
    private String message;
    private UUID transactionId;
    private BigDecimal amount;
    private String recipientEmail;
    private BigDecimal senderNewBalance;

    // Constructors
    public TransferResponse() {
    }

    public TransferResponse(boolean success, String message, UUID transactionId,
            BigDecimal amount, String recipientEmail, BigDecimal senderNewBalance) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
        this.amount = amount;
        this.recipientEmail = recipientEmail;
        this.senderNewBalance = senderNewBalance;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public BigDecimal getSenderNewBalance() {
        return senderNewBalance;
    }

    public void setSenderNewBalance(BigDecimal senderNewBalance) {
        this.senderNewBalance = senderNewBalance;
    }
}