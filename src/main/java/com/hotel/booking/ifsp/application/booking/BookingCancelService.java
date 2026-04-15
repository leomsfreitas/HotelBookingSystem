package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;

import java.util.Objects;

public class BookingCancelService {

    private final BookingRepository bookingRepository;

    public BookingCancelService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void cancelBooking(BookingId bookingId) {
        Objects.requireNonNull(bookingId, "Booking ID cannot be null");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + bookingId.value()));

        booking.cancel();
        bookingRepository.save(booking);
    }
}
