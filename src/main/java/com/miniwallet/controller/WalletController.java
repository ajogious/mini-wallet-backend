package com.miniwallet.controller;

import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/wallet")
@CrossOrigin(origins = "http://localhost:5173")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getWalletBalance(@AuthenticationPrincipal User user) {
        Optional<Wallet> walletOptional = walletService.getWalletByUser(user);

        if (walletOptional.isEmpty()) {
            // This should not happen as wallet is created automatically, but handle
            // gracefully
            Wallet newWallet = walletService.createWalletForUser(user);
            walletOptional = Optional.of(newWallet);
        }

        Wallet wallet = walletOptional.get();

        Map<String, Object> response = new HashMap<>();
        response.put("balance", wallet.getBalance());
        response.put("currency", "NGN");

        return ResponseEntity.ok(response);
    }
}