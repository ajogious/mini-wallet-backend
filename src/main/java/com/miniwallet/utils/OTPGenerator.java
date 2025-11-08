package com.miniwallet.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class OTPGenerator {
    private static final String NUMBERS = "0123456789";
    private static final SecureRandom random = new SecureRandom();

    public String generateOTP(int length) {
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return otp.toString();
    }

    public String generateSixDigitOTP() {
        return generateOTP(6);
    }
}