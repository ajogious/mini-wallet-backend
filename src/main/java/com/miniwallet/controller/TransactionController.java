package com.miniwallet.controller;

import com.miniwallet.dto.PagedResponse;
import com.miniwallet.dto.TransactionResponse;
import com.miniwallet.model.User;
import com.miniwallet.service.TransactionService;
import com.miniwallet.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<PagedResponse<TransactionResponse>> getTransactions(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactionsPage = transactionService.getTransactionsByUser(user, pageable);

        PagedResponse<TransactionResponse> response = new PagedResponse<>(
                transactionsPage.getContent(),
                transactionsPage.getNumber(),
                transactionsPage.getSize(),
                transactionsPage.getTotalElements(),
                transactionsPage.getTotalPages(),
                transactionsPage.isLast());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllTransactions(Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var transactions = transactionService.getTransactionsByUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactions);
        response.put("count", transactions.size());

        return ResponseEntity.ok(response);
    }
}
