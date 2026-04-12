package com.hotel.booking.ifsp.domain.booking;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public record Period(LocalDate checkIn, LocalDate checkOut) {

    public Period {
        Objects.requireNonNull(checkIn, "Check-in date cannot be null");
        Objects.requireNonNull(checkOut, "Check-out date cannot be null");
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
    }

    public long numberOfDays() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public boolean overlapsWith(Period other) {
        return checkIn.isBefore(other.checkOut) && checkOut.isAfter(other.checkIn);
    }
}
