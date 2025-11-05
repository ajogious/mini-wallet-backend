package com.miniwallet.service;

import com.miniwallet.dto.*;
import com.miniwallet.exception.CustomException;
import com.miniwallet.model.User;
import com.miniwallet.model.Wallet;
import com.miniwallet.repository.UserRepository;
import com.miniwallet.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException("Email is already registered", "EMAIL_ALREADY_EXISTS");
        }

        // Create new user
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(user);

        // Automatically create wallet for the user
        Wallet wallet = walletService.createWalletForUser(savedUser);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // Create user response with wallet balance
        UserResponse userResponse = new UserResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                wallet.getBalance());

        return new AuthResponse(token, "Registration successful", userResponse);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new CustomException("Invalid email or password", "INVALID_CREDENTIALS");
        }

        User user = userOptional.get();

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email or password", "INVALID_CREDENTIALS");
        }

        // Verify wallet exists, create if it doesn't
        Wallet wallet = walletService.getWalletByUser(user)
                .orElseGet(() -> walletService.createWalletForUser(user));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Create user response with wallet balance
        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                wallet.getBalance());

        return new AuthResponse(token, "Login successful", userResponse);
    }

}