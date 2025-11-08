package com.miniwallet.controller;

import com.miniwallet.dto.*;
import com.miniwallet.model.User;
import com.miniwallet.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> unifiedLogin(@Valid @RequestBody UnifiedLoginRequest loginRequest) {
        AuthResponse authResponse = authService.unifiedLogin(loginRequest);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOTP(@Valid @RequestBody OTPVerificationRequest otpRequest) {
        AuthResponse authResponse = authService.verifyOTP(otpRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<Map<String, Object>> resendOTP(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        authService.resendOTP(identifier);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OTP sent successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-bvn")
    public ResponseEntity<BVNVerificationResponse> verifyBVN(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BVNVerificationRequest bvnRequest) {
        BVNVerificationResponse response = authService.verifyBVN(bvnRequest, user);
        return ResponseEntity.ok(response);
    }

    // Keep old login endpoint for backward compatibility
    @PostMapping("/old-login")
    public ResponseEntity<AuthResponse> oldLogin(@Valid @RequestBody LoginRequest loginRequest) {
        // Convert to unified login
        UnifiedLoginRequest unifiedRequest = new UnifiedLoginRequest(loginRequest.getEmail(), loginRequest.getPassword());
        AuthResponse authResponse = authService.unifiedLogin(unifiedRequest);
        return ResponseEntity.ok(authResponse);
    }
}