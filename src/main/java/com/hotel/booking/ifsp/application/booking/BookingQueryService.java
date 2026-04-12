package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;
import com.hotel.booking.ifsp.domain.exception.GuestNotFoundException;
import com.hotel.booking.ifsp.domain.guest.Guest;
import com.hotel.booking.ifsp.domain.guest.GuestRepository;

import java.util.Objects;

public class BookingQueryService {

    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;

    public BookingQueryService(BookingRepository bookingRepository, GuestRepository guestRepository) {
        this.bookingRepository = bookingRepository;
        this.guestRepository = guestRepository;
    }

    public BookingDetails findBooking(BookingId bookingId) {
        Objects.requireNonNull(bookingId, "Booking ID cannot be null");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + bookingId.value()));

        Guest guest = guestRepository.findById(booking.getGuestId())
                .orElseThrow(() -> new GuestNotFoundException(
                        "Guest not found with id: " + booking.getGuestId().value()));

        return new BookingDetails(
                booking.getId(),
                guest.getName(),
                guest.getCpf().value(),
                booking.getRoomCategory(),
                booking.getPeriod(),
                booking.getTotalValue(),
                booking.getStatus()
        );
    }
}
