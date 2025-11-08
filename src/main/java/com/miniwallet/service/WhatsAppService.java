package com.miniwallet.service;

import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    public void sendOTP(String phoneNumber, String otp) {
        // TODO: Integrate with actual WhatsApp Business API
        // For now, we'll just log the OTP
        System.out.println("WhatsApp OTP for " + phoneNumber + ": " + otp);
        System.out.println("Message: Your Mini Wallet OTP is: " + otp + ". Valid for 10 minutes.");

        // Simulate API call delay
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}