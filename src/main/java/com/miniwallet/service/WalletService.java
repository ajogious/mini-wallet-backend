package com.miniwallet.service;

import com.miniwallet.dto.TransferRequest;
import com.miniwallet.dto.TransferResponse;
import com.miniwallet.model.Transaction;
import com.miniwallet.model.TransactionType;
import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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

    @Transactional
    public TransferResponse transfer(User sender, TransferRequest transferRequest) {
        System.out.println("=== STARTING TRANSFER ===");
        System.out.println("Sender: " + sender.getEmail() + " (ID: " + sender.getId() + ")");
        System.out.println("Recipient Email: " + transferRequest.getRecipientEmail());
        System.out.println("Amount: " + transferRequest.getAmount());

        // Validate amount
        if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Transfer amount must be greater than 0");
        }

        // Get sender's wallet
        Wallet senderWallet = getWalletByUser(sender)
                .orElseThrow(() -> new RuntimeException("Wallet not found for sender: " + sender.getEmail()));
        System.out.println("Sender Wallet ID: " + senderWallet.getId() + ", Balance: " + senderWallet.getBalance());

        // Check if sender has sufficient balance
        if (senderWallet.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance. Available: " + senderWallet.getBalance());
        }

        // Find recipient by email
        User recipient = userRepository.findByEmail(transferRequest.getRecipientEmail())
                .orElseThrow(() -> new RuntimeException(
                        "Recipient not found with email: " + transferRequest.getRecipientEmail()));
        System.out.println("Recipient Found: " + recipient.getEmail() + " (ID: " + recipient.getId() + ")");

        // Check if sender is trying to transfer to themselves
        if (sender.getId().equals(recipient.getId())) {
            throw new RuntimeException("Cannot transfer funds to yourself");
        }

        // Get recipient's wallet (create if doesn't exist)
        Wallet recipientWallet = getWalletByUser(recipient)
                .orElseGet(() -> {
                    System.out.println("Creating new wallet for recipient");
                    return createWalletForUser(recipient);
                });
        System.out.println(
                "Recipient Wallet ID: " + recipientWallet.getId() + ", Balance: " + recipientWallet.getBalance());

        System.out.println("Sender balance before: " + senderWallet.getBalance());
        System.out.println("Recipient balance before: " + recipientWallet.getBalance());

        // Perform transfer in a single transaction
        // Debit sender
        BigDecimal senderNewBalance = senderWallet.getBalance().subtract(transferRequest.getAmount());
        senderWallet.setBalance(senderNewBalance);
        walletRepository.save(senderWallet);
        System.out.println("Sender balance after debit: " + senderNewBalance);

        // Credit recipient
        BigDecimal recipientNewBalance = recipientWallet.getBalance().add(transferRequest.getAmount());
        recipientWallet.setBalance(recipientNewBalance);
        walletRepository.save(recipientWallet);
        System.out.println("Recipient balance after credit: " + recipientNewBalance);

        // Create transactions for both parties
        System.out.println("Creating sender transaction...");
        Transaction senderTransaction = transactionService.createTransaction(
                senderWallet,
                transferRequest.getAmount(),
                TransactionType.DEBIT,
                "Transfer to " + recipient.getEmail(),
                senderNewBalance);
        System.out.println("Sender Transaction ID: " + senderTransaction.getId());

        System.out.println("Creating recipient transaction...");
        Transaction recipientTransaction = transactionService.createTransaction(
                recipientWallet,
                transferRequest.getAmount(),
                TransactionType.CREDIT,
                "Transfer from " + sender.getEmail(),
                recipientNewBalance);
        System.out.println("Recipient Transaction ID: " + recipientTransaction.getId());

        System.out.println("=== TRANSFER COMPLETED ===");
        System.out.println("Sender new balance: " + senderNewBalance);
        System.out.println("Recipient new balance: " + recipientNewBalance);

        // Return transfer response
        return new TransferResponse(
                true,
                "Transfer successful to " + recipient.getEmail(),
                senderTransaction.getId(),
                transferRequest.getAmount(),
                recipient.getEmail(),
                senderNewBalance);
    }

    // @Transactional
    // public TransferResponse transfer(User sender, TransferRequest
    // transferRequest) {
    // // Validate amount
    // if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
    // throw new RuntimeException("Transfer amount must be greater than 0");
    // }

    // // Get sender's wallet
    // Wallet senderWallet = getWalletByUser(sender)
    // .orElseThrow(() -> new RuntimeException("Wallet not found for sender: " +
    // sender.getEmail()));

    // // Check if sender has sufficient balance
    // if (senderWallet.getBalance().compareTo(transferRequest.getAmount()) < 0) {
    // throw new RuntimeException("Insufficient balance. Available: " +
    // senderWallet.getBalance());
    // }

    // // Find recipient by email
    // User recipient =
    // userRepository.findByEmail(transferRequest.getRecipientEmail())
    // .orElseThrow(() -> new RuntimeException(
    // "Recipient not found with email: " + transferRequest.getRecipientEmail()));

    // // Check if sender is trying to transfer to themselves
    // if (sender.getId().equals(recipient.getId())) {
    // throw new RuntimeException("Cannot transfer funds to yourself");
    // }

    // // Get recipient's wallet (create if doesn't exist)
    // Wallet recipientWallet = getWalletByUser(recipient)
    // .orElseGet(() -> createWalletForUser(recipient));

    // // Perform transfer in a single transaction
    // // Debit sender
    // BigDecimal senderNewBalance =
    // senderWallet.getBalance().subtract(transferRequest.getAmount());
    // senderWallet.setBalance(senderNewBalance);
    // walletRepository.save(senderWallet);

    // // Credit recipient
    // BigDecimal recipientNewBalance =
    // recipientWallet.getBalance().add(transferRequest.getAmount());
    // recipientWallet.setBalance(recipientNewBalance);
    // walletRepository.save(recipientWallet);

    // // Create transactions for both parties
    // Transaction senderTransaction = transactionService.createTransaction(
    // senderWallet,
    // transferRequest.getAmount(),
    // TransactionType.DEBIT,
    // "Transfer to " + recipient.getEmail(),
    // senderNewBalance);

    // // Return transfer response
    // return new TransferResponse(
    // true,
    // "Transfer successful to " + recipient.getEmail(),
    // senderTransaction.getId(),
    // transferRequest.getAmount(),
    // recipient.getEmail(),
    // senderNewBalance);
    // }

}