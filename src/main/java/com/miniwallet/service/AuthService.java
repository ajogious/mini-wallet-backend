package com.miniwallet.service;

import com.miniwallet.dto.*;
import com.miniwallet.exception.CustomException;
import com.miniwallet.model.*;
import com.miniwallet.repository.*;
import com.miniwallet.utils.JwtUtil;
import com.miniwallet.utils.OTPGenerator;
import com.miniwallet.utils.PhoneValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private BVNVerificationRepository bvnVerificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private OTPGenerator otpGenerator;

    @Autowired
    private PhoneValidator phoneValidator;

    @Autowired
    private WhatsAppService whatsAppService;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Validate phone number if provided
        if (registerRequest.getPhoneNumber() != null && !registerRequest.getPhoneNumber().isEmpty()) {
            if (!phoneValidator.isValidNigerianPhone(registerRequest.getPhoneNumber())) {
                throw new CustomException("Please provide a valid Nigerian phone number", "INVALID_PHONE");
            }

            // Format phone number
            String formattedPhone = phoneValidator.formatPhone(registerRequest.getPhoneNumber());
            registerRequest.setPhoneNumber(formattedPhone);

            // Check if phone number already exists
            if (userRepository.existsByPhoneNumber(formattedPhone)) {
                throw new CustomException("Phone number is already registered", "PHONE_ALREADY_EXISTS");
            }
        }

        // Check if user already exists with email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException("Email is already registered", "EMAIL_ALREADY_EXISTS");
        }

        // Check BVN uniqueness if provided
        if (registerRequest.getBvn() != null && !registerRequest.getBvn().isEmpty()) {
            if (userRepository.existsByBvn(registerRequest.getBvn())) {
                throw new CustomException("BVN is already registered with another account", "BVN_ALREADY_EXISTS");
            }
        }

        // Create new user
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        // Set optional fields
        if (registerRequest.getPhoneNumber() != null && !registerRequest.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(registerRequest.getPhoneNumber());
        }

        // Set verification status and transaction limit
        user.setVerificationStatus(VerificationStatus.PENDING);
        user.setTransactionLimit(new BigDecimal("100000.00")); // Default limit for unverified users

        // Set BVN if provided (encrypted)
        if (registerRequest.getBvn() != null && !registerRequest.getBvn().isEmpty()) {
            user.setBvn(passwordEncoder.encode(registerRequest.getBvn()));
        }

        User savedUser = userRepository.save(user);

        // Create BVN verification record if BVN provided
        if (registerRequest.getBvn() != null && !registerRequest.getBvn().isEmpty()) {
            BVNVerification bvnVerification = new BVNVerification(
                    registerRequest.getBvn(),
                    registerRequest.getFirstName(),
                    registerRequest.getLastName(),
                    registerRequest.getOtherName(),
                    registerRequest.getDateOfBirth(),
                    savedUser.getPhoneNumber(),
                    savedUser
            );
            bvnVerificationRepository.save(bvnVerification);

            // Auto-verify for demo (replace with actual BVN verification service)
            performBVNVerification(savedUser, bvnVerification);
        }

        // Automatically create wallet for the user
        Wallet wallet = walletService.createWalletForUser(savedUser);

        // Generate JWT token (for immediate login after registration)
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // Create user response with both wallet balance and user details
        UserResponse userResponse = createUserResponse(savedUser, wallet.getBalance());

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

        // Check if OTP verification is required (for verified users with phone)
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() &&
                user.getVerificationStatus() == VerificationStatus.VERIFIED) {
            return initiateOTPLogin(user, loginRequest.getEmail());
        }

        // Traditional login without OTP
        return completeLogin(user);
    }

    public AuthResponse unifiedLogin(UnifiedLoginRequest loginRequest) {
        // Find user by email or phone
        String identifier = loginRequest.getIdentifier().replaceAll("\\s+", "").trim();
        if(identifier.startsWith("0")) {
            identifier = "+234" + identifier.substring(1);
        }

        Optional<User> userOptional = userRepository.findByEmailOrPhoneNumber(
                identifier, identifier
        );

        if (userOptional.isEmpty()) {
            throw new CustomException("Invalid email/phone or password", "INVALID_CREDENTIALS");
        }

        User user = userOptional.get();

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new CustomException("Invalid email/phone or password", "INVALID_CREDENTIALS");
        }

        // Check if user is verified and has phone for OTP
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
            return initiateOTPLogin(user, identifier);
        }

        System.out.println("Identifier: " + loginRequest.getIdentifier());
        userRepository.findByEmailOrPhoneNumber(
                loginRequest.getIdentifier(),
                loginRequest.getIdentifier()
        ).ifPresentOrElse(
                u -> System.out.println("Found user: " + u.getEmail() + " | " + u.getPhoneNumber()),
                () -> System.out.println("No user found")
        );


        // If not verified or no phone, proceed with traditional login
        return completeLogin(user);
    }


    private AuthResponse initiateOTPLogin(User user, String identifier) {
        // Generate and send OTP
        String otp = otpGenerator.generateSixDigitOTP();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10); // OTP valid for 10 minutes

        // Save login attempt
        LoginAttempt loginAttempt = new LoginAttempt(identifier, otp, expiry);
        loginAttemptRepository.save(loginAttempt);

        // Send OTP via WhatsApp
        try {
            whatsAppService.sendOTP(user.getPhoneNumber(), otp);
        } catch (Exception e) {
            // If OTP sending fails, fall back to traditional login
            return completeLogin(user);
        }

        // Return response indicating OTP is required
        UserResponse userResponse = createUserResponse(user, null);
        return new AuthResponse(null, "OTP sent to your WhatsApp", userResponse, true);
    }

    private AuthResponse completeLogin(User user) {
        // Verify wallet exists, create if it doesn't
        Wallet wallet = walletService.getWalletByUser(user)
                .orElseGet(() -> walletService.createWalletForUser(user));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Create user response with wallet balance
        UserResponse userResponse = createUserResponse(user, wallet.getBalance());

        return new AuthResponse(token, "Login successful", userResponse);
    }

    public AuthResponse verifyOTP(OTPVerificationRequest otpRequest) {
        // Find the latest valid OTP for the identifier
        Optional<LoginAttempt> loginAttemptOptional = loginAttemptRepository
                .findByIdentifierAndOtpAndUsedFalse(otpRequest.getIdentifier(), otpRequest.getOtp());

        if (loginAttemptOptional.isEmpty()) {
            throw new CustomException("Invalid or expired OTP", "INVALID_OTP");
        }

        LoginAttempt loginAttempt = loginAttemptOptional.get();

        if (!loginAttempt.isValid()) {
            throw new CustomException("OTP has expired", "OTP_EXPIRED");
        }

        // Mark OTP as used
        loginAttempt.setUsed(true);
        loginAttemptRepository.save(loginAttempt);

        // Find user
        Optional<User> userOptional = userRepository.findByEmailOrPhoneNumber(
                otpRequest.getIdentifier(),
                otpRequest.getIdentifier()
        );

        if (userOptional.isEmpty()) {
            throw new CustomException("User not found", "USER_NOT_FOUND");
        }

        User user = userOptional.get();

        // Complete login with OTP verification
        Wallet wallet = walletService.getWalletByUser(user)
                .orElseGet(() -> walletService.createWalletForUser(user));

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        // Create user response with wallet balance
        UserResponse userResponse = createUserResponse(user, wallet.getBalance());

        return new AuthResponse(token, "Login successful", userResponse);
    }

    public BVNVerificationResponse verifyBVN(BVNVerificationRequest bvnRequest, User user) {
        // Check if BVN is already verified for this user
        if (user.getVerificationStatus() == VerificationStatus.VERIFIED) {
            throw new CustomException("Account is already verified", "ALREADY_VERIFIED");
        }

        // Check if BVN is already used by another user
        if (userRepository.existsByBvn(bvnRequest.getBvn())) {
            throw new CustomException("BVN is already registered with another account", "BVN_ALREADY_EXISTS");
        }

        // Create BVN verification record
        BVNVerification bvnVerification = new BVNVerification(
                bvnRequest.getBvn(),
                user.getFirstName(),
                user.getLastName(),
                user.getOtherName(),
                bvnRequest.getDateOfBirth(),
                user.getPhoneNumber(),
                user
        );

        BVNVerification savedVerification = bvnVerificationRepository.save(bvnVerification);

        // Perform BVN verification
        return performBVNVerification(user, savedVerification);
    }

    private BVNVerificationResponse performBVNVerification(User user, BVNVerification bvnVerification) {
        try {
            // TODO: Integrate with actual BVN verification service
            // For demo purposes, we'll auto-verify with some conditions

            boolean isVerified = true;
            String message = "BVN verified successfully";

            // Demo validation - check if BVN is 11 digits
            if (bvnVerification.getBvn().length() != 11) {
                isVerified = false;
                message = "Invalid BVN format";
            }

            if (isVerified) {
                // Update user verification status
                user.setVerificationStatus(VerificationStatus.VERIFIED);
                user.setBvn(passwordEncoder.encode(bvnVerification.getBvn()));
                user.setTransactionLimit(new BigDecimal("5000000.00")); // ₦5,000,000 limit for verified users

                // TODO: Generate virtual account via Monnify or other provider
                // user.setVirtualAccountNumber(generatedAccountNumber);
                // user.setBankName("Monnify");

                userRepository.save(user);

                bvnVerification.setStatus(VerificationStatus.VERIFIED);
                bvnVerification.setResponseMessage(message);
            } else {
                bvnVerification.setStatus(VerificationStatus.REJECTED);
                bvnVerification.setResponseMessage(message);
            }

            bvnVerificationRepository.save(bvnVerification);

            return new BVNVerificationResponse(
                    isVerified,
                    message,
                    user.getVerificationStatus(),
                    user.getTransactionLimit()
            );

        } catch (Exception e) {
            bvnVerification.setStatus(VerificationStatus.REJECTED);
            bvnVerification.setResponseMessage("BVN verification failed: " + e.getMessage());
            bvnVerificationRepository.save(bvnVerification);

            throw new CustomException("BVN verification failed", "BVN_VERIFICATION_FAILED");
        }
    }

    public void resendOTP(String identifier) {
        // Check if there's a recent OTP that hasn't expired
        Optional<LoginAttempt> recentAttempt = loginAttemptRepository
                .findTopByIdentifierOrderByCreatedAtDesc(identifier);

        if (recentAttempt.isPresent() && recentAttempt.get().isValid()) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastSent = recentAttempt.get().getCreatedAt();

            // Prevent OTP spam - wait at least 1 minute between requests
            if (lastSent.plusMinutes(1).isAfter(now)) {
                throw new CustomException("Please wait before requesting a new OTP", "OTP_REQUEST_LIMIT");
            }
        }

        // Find user
        Optional<User> userOptional = userRepository.findByEmailOrPhoneNumber(identifier, identifier);
        if (userOptional.isEmpty()) {
            throw new CustomException("User not found", "USER_NOT_FOUND");
        }

        User user = userOptional.get();

        // Generate new OTP
        String otp = otpGenerator.generateSixDigitOTP();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(10);

        // Save login attempt
        LoginAttempt loginAttempt = new LoginAttempt(identifier, otp, expiry);
        loginAttemptRepository.save(loginAttempt);

        // Send OTP via WhatsApp
        try {
            whatsAppService.sendOTP(user.getPhoneNumber(), otp);
        } catch (Exception e) {
            throw new CustomException("Failed to send OTP. Please try again.", "OTP_SEND_FAILED");
        }
    }

    private UserResponse createUserResponse(User user, BigDecimal walletBalance) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getVerificationStatus(),
                user.getTransactionLimit(),
                user.getVirtualAccountNumber(),
                user.getBankName());
    }
}