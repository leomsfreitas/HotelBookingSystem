package com.hotel.booking.ifsp.domain.booking;

import java.util.Objects;
import java.util.UUID;

public record BookingId(UUID value) {

    public BookingId {
        Objects.requireNonNull(value, "Booking ID cannot be null");
    }

    public static BookingId generate() {
        return new BookingId(UUID.randomUUID());
    }
}
