package com.miniwallet.repository;

import com.miniwallet.model.Transaction;
import com.miniwallet.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByWalletOrderByTimestampDesc(Wallet wallet);

    Page<Transaction> findByWalletOrderByTimestampDesc(Wallet wallet, Pageable pageable);

    List<Transaction> findByWallet(Wallet wallet);
}