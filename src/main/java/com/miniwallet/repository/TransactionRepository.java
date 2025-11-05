package com.miniwallet.repository;

import com.miniwallet.model.Transaction;
import com.miniwallet.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.wallet = :wallet ORDER BY t.timestamp DESC")
    List<Transaction> findByWalletOrderByTimestampDesc(@Param("wallet") Wallet wallet);

    @Query("SELECT t FROM Transaction t WHERE t.wallet = :wallet ORDER BY t.timestamp DESC")
    Page<Transaction> findByWalletOrderByTimestampDesc(@Param("wallet") Wallet wallet, Pageable pageable);

    List<Transaction> findByWallet(Wallet wallet);
}