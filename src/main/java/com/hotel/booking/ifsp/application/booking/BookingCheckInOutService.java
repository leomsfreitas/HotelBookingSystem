package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;

import java.time.LocalDate;
import java.util.Objects;

public class BookingCheckInOutService {

    private final BookingRepository bookingRepository;

    public BookingCheckInOutService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void checkIn(BookingId bookingId) {
        Objects.requireNonNull(bookingId, "Booking ID cannot be null");
        Booking booking = findBooking(bookingId);
        
        if (LocalDate.now().isBefore(booking.getPeriod().checkIn())) {
            throw new IllegalStateException("Cannot check-in before the scheduled date");
        }
        
        booking.checkIn();
        bookingRepository.save(booking);
    }

    public void checkOut(BookingId bookingId) {
        Objects.requireNonNull(bookingId, "Booking ID cannot be null");
        Booking booking = findBooking(bookingId);

        if (LocalDate.now().isBefore(booking.getPeriod().checkIn())) {
            throw new IllegalStateException("Cannot check-out before the scheduled check-in date");
        }

        booking.checkOut();
        bookingRepository.save(booking);
    }

    private Booking findBooking(BookingId bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + bookingId.value()));
    }
}
