package com.miniwallet.controller;

import com.miniwallet.dto.PagedResponse;
import com.miniwallet.dto.TransactionResponse;
import com.miniwallet.model.User;
import com.miniwallet.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<PagedResponse<TransactionResponse>> getTransactions(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

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
    public ResponseEntity<Map<String, Object>> getAllTransactions(@AuthenticationPrincipal User user) {
        var transactions = transactionService.getTransactionsByUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactions);
        response.put("count", transactions.size());

        return ResponseEntity.ok(response);
    }
}