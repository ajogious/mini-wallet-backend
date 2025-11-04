package com.miniwallet.config;

import com.miniwallet.model.TransactionType;
import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.UserRepository;
import com.miniwallet.service.TransactionService;
import com.miniwallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    @Override
    public void run(String... args) throws Exception {
        // Create sample transactions for existing users for testing
        createSampleTransactions();
    }

    private void createSampleTransactions() {
        // Check if we have any users
        var users = userRepository.findAll();
        if (users.isEmpty()) {
            return;
        }

        // For each user, create some sample transactions if they don't have any
        for (User user : users) {
            Optional<Wallet> walletOpt = walletService.getWalletByUser(user);
            if (walletOpt.isPresent()) {
                Wallet wallet = walletOpt.get();

                // Check if user already has transactions
                var existingTransactions = transactionService.getTransactionsByWallet(wallet);
                if (existingTransactions.isEmpty()) {

                    // Create some sample transactions
                    transactionService.createTransaction(
                            wallet,
                            new BigDecimal("1000.00"),
                            TransactionType.CREDIT,
                            "Initial deposit",
                            new BigDecimal("1000.00"));

                    transactionService.createTransaction(
                            wallet,
                            new BigDecimal("250.50"),
                            TransactionType.DEBIT,
                            "Transfer to John Doe",
                            new BigDecimal("749.50"));

                    transactionService.createTransaction(
                            wallet,
                            new BigDecimal("500.00"),
                            TransactionType.CREDIT,
                            "Fund wallet",
                            new BigDecimal("1249.50"));
                }
            }
        }
    }
}