package com.hotel.booking.ifsp.domain.guest;

import java.util.Objects;

public record CPF(String value) {

    public CPF {
        Objects.requireNonNull(value, "CPF cannot be null");
        String digits = value.replaceAll("[.\\-]", "");
        if (!isValid(digits)) {
            throw new IllegalArgumentException("Invalid CPF: " + value);
        }
    }

    private static boolean isValid(String digits) {
        if (digits.length() != 11) return false;
        if (digits.chars().distinct().count() == 1) return false;

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * (10 - i);
        }
        int firstDigit = 11 - (sum % 11);
        if (firstDigit >= 10) firstDigit = 0;
        if (firstDigit != Character.getNumericValue(digits.charAt(9))) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Character.getNumericValue(digits.charAt(i)) * (11 - i);
        }
        int secondDigit = 11 - (sum % 11);
        if (secondDigit >= 10) secondDigit = 0;
        return secondDigit == Character.getNumericValue(digits.charAt(10));
    }

    @Override
    public String toString() {
        return value;
    }
}
