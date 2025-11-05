package com.miniwallet.controller;

import com.miniwallet.dto.DepositRequest;
import com.miniwallet.dto.PinVerificationRequest;
import com.miniwallet.dto.TransferRequest;
import com.miniwallet.dto.TransferResponse;
import com.miniwallet.exception.CustomException;
import com.miniwallet.model.Transaction;
import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.UserRepository;
import com.miniwallet.service.WalletService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wallet")
@CrossOrigin(origins = "http://localhost:5173")
public class WalletController {

        @Autowired
        private WalletService walletService;

        @Autowired
        private UserRepository userRepository;

        @GetMapping
        public ResponseEntity<Map<String, Object>> getWallet(Principal principal) {
                String email = principal.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Wallet wallet = walletService.getWalletByUser(user)
                                .orElseGet(() -> walletService.createWalletForUser(user));

                Map<String, Object> response = new HashMap<>();
                response.put("id", wallet.getId());
                response.put("balance", wallet.getBalance());
                response.put("currency", "NGN");
                response.put("createdAt", wallet.getCreatedAt());
                response.put("updatedAt", wallet.getUpdatedAt());

                return ResponseEntity.ok(response);
        }

        @GetMapping("/balance")
        public ResponseEntity<Map<String, Object>> getWalletBalance(Principal principal) {
                String email = principal.getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Wallet wallet = walletService.getWalletByUser(user)
                                .orElseGet(() -> walletService.createWalletForUser(user));

                Map<String, Object> response = new HashMap<>();
                response.put("balance", wallet.getBalance());
                response.put("currency", "NGN");

                return ResponseEntity.ok(response);
        }

        @PostMapping("/deposit")
        public ResponseEntity<Map<String, Object>> deposit(
                        Principal principal,
                        @Valid @RequestBody DepositRequest depositRequest) {

                try {
                        String email = principal.getName();
                        User user = userRepository.findByEmail(email)
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                        Transaction transaction = walletService.deposit(user, depositRequest.getAmount());

                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "Deposit successful");
                        response.put("transactionId", transaction.getId());
                        response.put("amount", transaction.getAmount());
                        response.put("newBalance", transaction.getBalanceAfterTransaction());
                        response.put("transactionType", transaction.getTransactionType());

                        return ResponseEntity.ok(response);

                } catch (RuntimeException e) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", e.getMessage());
                        return ResponseEntity.badRequest().body(errorResponse);
                }
        }

        // @PostMapping("/transfer")
        // public ResponseEntity<?> transfer(
        // Principal principal,
        // @Valid @RequestBody TransferRequest transferRequest) {

        // try {
        // String email = principal.getName();
        // User user = userRepository.findByEmail(email)
        // .orElseThrow(() -> new RuntimeException("User not found"));

        // TransferResponse transferResponse = walletService.transfer(user,
        // transferRequest);
        // return ResponseEntity.ok(transferResponse);

        // } catch (RuntimeException e) {
        // Map<String, Object> errorResponse = new HashMap<>();
        // errorResponse.put("success", false);
        // errorResponse.put("message", e.getMessage());
        // return ResponseEntity.badRequest().body(errorResponse);
        // }
        // }

        @PostMapping("/transfer")
        public ResponseEntity<?> transfer(
                        Principal principal,
                        @Valid @RequestBody TransferRequest transferRequest) {

                try {
                        String email = principal.getName();
                        User user = userRepository.findByEmail(email)
                                        .orElseThrow(() -> new RuntimeException("User not found"));

                        // ✅ Include PIN from the request
                        TransferResponse transferResponse = walletService.transfer(user, transferRequest);

                        return ResponseEntity.ok(transferResponse);

                } catch (RuntimeException e) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", e.getMessage());
                        return ResponseEntity.badRequest().body(errorResponse);
                }
        }

        @PostMapping("/verify-pin")
        public ResponseEntity<?> verifyPin(
                        Principal principal,
                        @Valid @RequestBody PinVerificationRequest pinRequest) {

                try {
                        // Extract the user's email from JWT principal
                        String email = principal.getName();

                        // Fetch the full User entity
                        User user = userRepository.findByEmail(email)
                                        .orElseThrow(() -> new CustomException("User not found", "USER_NOT_FOUND"));

                        boolean isValid = walletService.verifyPin(user, pinRequest.getPin());

                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("valid", isValid);
                        response.put("message", isValid ? "PIN verified successfully" : "Invalid PIN");

                        return ResponseEntity.ok(response);

                } catch (CustomException e) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", e.getMessage());
                        return ResponseEntity.badRequest().body(errorResponse);
                }
        }

        @PostMapping("/update-pin")
        public ResponseEntity<?> updatePin(
                        Principal principal,
                        @Valid @RequestBody PinVerificationRequest pinRequest) {

                try {
                        // Extract the user's email
                        String email = principal.getName();

                        // Fetch actual User entity
                        User user = userRepository.findByEmail(email)
                                        .orElseThrow(() -> new CustomException("User not found", "USER_NOT_FOUND"));

                        walletService.updatePin(user, pinRequest.getPin());

                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("message", "PIN updated successfully");

                        return ResponseEntity.ok(response);

                } catch (CustomException e) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("message", e.getMessage());
                        return ResponseEntity.badRequest().body(errorResponse);
                }
        }

}