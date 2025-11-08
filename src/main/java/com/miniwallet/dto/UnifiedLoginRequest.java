package com.miniwallet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UnifiedLoginRequest {

    // Getters and Setters
    @NotBlank(message = "Email or phone number is required")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;

    // Constructors
    public UnifiedLoginRequest(String identifier, String password) {
        this.identifier = identifier;
        this.password = password;
    }

}