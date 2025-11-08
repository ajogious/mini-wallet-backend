package com.miniwallet.repository;

import com.miniwallet.model.BVNVerification;
import com.miniwallet.model.User;
import com.miniwallet.model.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BVNVerificationRepository extends JpaRepository<BVNVerification, Long> {
    Optional<BVNVerification> findByBvn(String bvn);
    Optional<BVNVerification> findByUser(User user);
    List<BVNVerification> findByStatus(VerificationStatus status);
    boolean existsByBvn(String bvn);
}