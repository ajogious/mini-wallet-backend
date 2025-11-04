package com.miniwallet.service;

import com.miniwallet.dto.TransactionResponse;
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

        // return transactionRepository.save(transaction);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setDescription(description);
        transaction.setBalanceAfterTransaction(balanceAfterTransaction);
        transaction.setWallet(wallet);
        return transactionRepository.save(transaction);
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

    @SuppressWarnings("null")
    public Page<TransactionResponse> getTransactionsByUser(User user, Pageable pageable) {
        Optional<Wallet> walletOptional = walletService.getWalletByUser(user);
        if (walletOptional.isEmpty()) {
            return Page.empty(pageable);
        }

        Wallet wallet = walletOptional.get();

        // Ensure we sort by timestamp descending
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<Transaction> transactionsPage = transactionRepository.findByWalletOrderByTimestampDesc(wallet,
                sortedPageable);

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
}