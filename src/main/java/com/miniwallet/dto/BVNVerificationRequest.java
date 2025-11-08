package com.miniwallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BVNVerificationRequest {

    // Getters and Setters
    @NotBlank(message = "BVN is required")
    @Size(min = 11, max = 11, message = "BVN must be 11 digits")
    @Pattern(regexp = "\\d{11}", message = "BVN must contain only digits")
    private String bvn;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth; // Format: DD-MM-YYYY

    // Constructors
    public BVNVerificationRequest() {}

    public BVNVerificationRequest(String bvn, String dateOfBirth) {
        this.bvn = bvn;
        this.dateOfBirth = dateOfBirth;
    }

}