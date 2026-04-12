package com.hotel.booking.ifsp.domain.exception;

public class GuestNotFoundException extends RuntimeException {

    public GuestNotFoundException(String message) {
        super(message);
    }
}
