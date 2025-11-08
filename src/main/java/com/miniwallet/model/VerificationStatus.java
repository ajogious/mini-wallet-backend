package com.miniwallet.model;

public enum VerificationStatus {
    PENDING,    // User registered but not verified
    VERIFIED,   // BVN/NIN verified, can transact
    REJECTED,   // Verification failed
    UNDER_REVIEW // Manual review required
}