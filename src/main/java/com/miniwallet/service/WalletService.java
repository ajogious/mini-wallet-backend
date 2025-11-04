package com.miniwallet.service;

import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

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
}