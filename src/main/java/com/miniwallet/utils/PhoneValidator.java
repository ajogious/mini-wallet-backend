package com.miniwallet.utils;

import org.springframework.stereotype.Component;

@Component
public class PhoneValidator {

    public boolean isValidNigerianPhone(String phone) {
        if (phone == null) return false;

        // Remove any spaces or dashes
        String cleanedPhone = phone.replaceAll("[\\s-]+", "");

        // Format: +2348012345678 or 08012345678 or 2348012345678
        return cleanedPhone.matches("^(\\+?234|0)[7-9][0-1]\\d{8}$");
    }

    public String formatPhone(String phone) {
        if (phone == null) return null;

        String cleanedPhone = phone.replaceAll("[\\s-]+", "");

        if (cleanedPhone.startsWith("0")) {
            return "+234" + cleanedPhone.substring(1);
        } else if (cleanedPhone.startsWith("234")) {
            return "+" + cleanedPhone;
        } else if (cleanedPhone.startsWith("+234")) {
            return cleanedPhone;
        }

        return null; // Invalid format
    }

    public String getStandardFormat(String phone) {
        String formatted = formatPhone(phone);
        if (formatted != null) {
            return formatted.substring(1); // Return 2348012345678
        }
        return null;
    }
}