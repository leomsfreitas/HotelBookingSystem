package com.hotel.booking.ifsp.domain.room;

import java.math.BigDecimal;

public enum RoomCategory {

    STANDARD(new BigDecimal("150.00")),
    DELUXE(new BigDecimal("250.00")),
    SUITE(new BigDecimal("400.00"));

    private final BigDecimal dailyRate;

    RoomCategory(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }
}
