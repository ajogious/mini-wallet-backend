package com.miniwallet.dto;

import com.miniwallet.model.TransactionType;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
public class TransactionResponse {
    private UUID id;
    private BigDecimal amount;
    private TransactionType transactionType;
    private String description;
    private BigDecimal balanceAfterTransaction;
    private LocalDateTime timestamp;

    // Constructors
    public TransactionResponse(UUID id, BigDecimal amount, TransactionType transactionType,
            String description, BigDecimal balanceAfterTransaction, LocalDateTime timestamp) {
        this.id = id;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.timestamp = timestamp;
    }

}