package com.miniwallet.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String message;
    private UserResponse user;
    private boolean otpRequired;

//    Constructors
    public AuthResponse(String token, String message, UserResponse user) {
        this.token = token;
        this.message = message;
        this.user = user;
        this.otpRequired = false;
    }

    public AuthResponse(String token, String message, UserResponse user, boolean otpRequired) {
        this.token = token;
        this.message = message;
        this.user = user;
        this.otpRequired = otpRequired;
    }
}
