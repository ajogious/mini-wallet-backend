package com.miniwallet.controller;

import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.UserRepository;
import com.miniwallet.service.WalletService;
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
        String email = principal.getName(); // extracted from JWT

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletService.getWalletByUser(user)
                .orElseGet(() -> walletService.createWalletForUser(user));

        Map<String, Object> response = new HashMap<>();
        response.put("balance", wallet.getBalance());
        response.put("currency", "NGN");

        return ResponseEntity.ok(response);
    }

}