package com.miniwallet.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2, name = "balance_after_transaction")
    private BigDecimal balanceAfterTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    public Transaction(BigDecimal amount, TransactionType transactionType, String description,
            BigDecimal balanceAfterTransaction, Wallet wallet) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.balanceAfterTransaction = balanceAfterTransaction;
        this.wallet = wallet;
    }
}
