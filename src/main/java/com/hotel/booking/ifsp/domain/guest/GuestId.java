package com.hotel.booking.ifsp.domain.guest;

import java.util.Objects;
import java.util.UUID;

public record GuestId(UUID value) {

    public GuestId {
        Objects.requireNonNull(value, "Guest ID cannot be null");
    }

    public static GuestId generate() {
        return new GuestId(UUID.randomUUID());
    }
}
