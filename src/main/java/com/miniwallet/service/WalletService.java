package com.miniwallet.service;

import com.miniwallet.model.Transaction;
import com.miniwallet.model.TransactionType;
import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionService transactionService;

    public Wallet createWalletForUser(User user) {
        if (walletRepository.existsByUser(user)) {
            throw new RuntimeException("Wallet already exists for user: " + user.getEmail());
        }

        Wallet wallet = new Wallet(user);
        return walletRepository.save(wallet);
    }

    public Optional<Wallet> getWalletByUser(User user) {
        return walletRepository.findByUser(user);
    }

    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletRepository.findByUserId(userId);
    }

    @SuppressWarnings("null")
    public Wallet saveWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }

    @Transactional
    public Transaction deposit(User user, BigDecimal amount) {
        // Get user's wallet
        Wallet wallet = getWalletByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user: " + user.getEmail()));

        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Deposit amount must be greater than 0");
        }

        // Calculate new balance
        BigDecimal newBalance = wallet.getBalance().add(amount);

        // Update wallet balance
        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        // Create transaction record
        String description = "Wallet deposit";
        Transaction transaction = transactionService.createTransaction(
                wallet,
                amount,
                TransactionType.CREDIT,
                description,
                newBalance);

        return transaction;
    }
}