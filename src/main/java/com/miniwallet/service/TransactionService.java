package com.miniwallet.service;

import com.miniwallet.dto.TransactionResponse;
import com.miniwallet.exception.CustomException;
import com.miniwallet.model.Transaction;
import com.miniwallet.model.TransactionType;
import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.TransactionRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    @Transactional
    public Transaction createTransaction(Wallet wallet, BigDecimal amount, TransactionType type, String description,
            BigDecimal balanceAfterTransaction) {
        System.out.println(">>> Creating transaction in database:");
        System.out.println("    Wallet ID: " + wallet.getId());
        System.out.println("    Amount: " + amount);
        System.out.println("    Type: " + type);
        System.out.println("    Description: " + description);
        System.out.println("    Balance After: " + balanceAfterTransaction);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setBalanceAfterTransaction(balanceAfterTransaction);
        transaction.setWallet(wallet);

        Transaction savedTransaction = transactionRepository.save(transaction);
        System.out.println(">>> Transaction saved with ID: " + savedTransaction.getId());

        return savedTransaction;
    }

    public List<TransactionResponse> getTransactionsByUser(User user) {
        Optional<Wallet> walletOptional = walletService.getWalletByUser(user);
        if (walletOptional.isEmpty()) {
            return List.of();
        }

        Wallet wallet = walletOptional.get();
        List<Transaction> transactions = transactionRepository.findByWalletOrderByTimestampDesc(wallet);

        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<TransactionResponse> getTransactionsByUser(User user, Pageable pageable) {
        System.out.println("=== FETCHING TRANSACTIONS FOR USER ===");
        System.out.println("User: " + user.getEmail() + " (ID: " + user.getId() + ")");

        Optional<Wallet> walletOptional = walletService.getWalletByUser(user);
        if (walletOptional.isEmpty()) {
            System.out.println("!!! No wallet found for user: " + user.getEmail());
            return Page.empty(pageable);
        }

        Wallet wallet = walletOptional.get();
        System.out.println("User Wallet ID: " + wallet.getId());

        // Ensure we sort by timestamp descending
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<Transaction> transactionsPage = transactionRepository.findByWalletOrderByTimestampDesc(wallet,
                sortedPageable);

        System.out.println(
                ">>> Found " + transactionsPage.getTotalElements() + " transactions for user: " + user.getEmail());

        // Log each transaction found
        transactionsPage.getContent().forEach(transaction -> {
            System.out.println("    Transaction: " + transaction.getId() +
                    " | " + transaction.getTransactionType() +
                    " | " + transaction.getAmount() +
                    " | " + transaction.getDescription());
        });

        return transactionsPage.map(this::convertToResponse);
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getDescription(),
                transaction.getBalanceAfterTransaction(),
                transaction.getTimestamp());
    }

    public List<Transaction> getTransactionsByWallet(Wallet wallet) {
        return transactionRepository.findByWallet(wallet);
    }

    public TransactionResponse getTransactionDetails(User user, UUID transactionId) {
        // Find the transaction
        Optional<Transaction> transactionOpt = transactionRepository.findById(transactionId);
        if (transactionOpt.isEmpty()) {
            throw new CustomException("Transaction not found", "TRANSACTION_NOT_FOUND");
        }

        Transaction transaction = transactionOpt.get();

        // Verify the transaction belongs to the authenticated user
        Optional<Wallet> userWallet = walletService.getWalletByUser(user);
        if (userWallet.isEmpty() || !transaction.getWallet().getId().equals(userWallet.get().getId())) {
            throw new CustomException("Access denied to transaction", "ACCESS_DENIED");
        }

        return convertToResponse(transaction);
    }
}