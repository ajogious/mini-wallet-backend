package com.miniwallet.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PinVerificationRequest {

    @Size(min = 4, max = 4, message = "PIN must be exactly 4 digits")
    @Pattern(regexp = "\\d{4}", message = "PIN must contain only digits")
    private String pin;

    // Constructors
    public PinVerificationRequest(String pin) {
        this.pin = pin;
    }
}