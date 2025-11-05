package com.miniwallet.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PinVerificationRequest {

    @Size(min = 4, max = 4, message = "PIN must be exactly 4 digits")
    @Pattern(regexp = "\\d{4}", message = "PIN must contain only digits")
    private String pin;

    // Constructors
    public PinVerificationRequest() {
    }

    public PinVerificationRequest(String pin) {
        this.pin = pin;
    }

    // Getters and Setters
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}