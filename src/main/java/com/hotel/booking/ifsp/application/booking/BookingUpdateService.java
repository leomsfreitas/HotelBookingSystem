package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;
import com.hotel.booking.ifsp.domain.exception.RoomNotAvailableException;
import com.hotel.booking.ifsp.domain.room.RoomCategory;

import java.util.Objects;

public class BookingUpdateService {

    private final BookingRepository bookingRepository;

    public BookingUpdateService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking updateBooking(BookingId bookingId, RoomCategory newRoomCategory, Period newPeriod) {
        Objects.requireNonNull(bookingId, "Booking ID cannot be null");
        Objects.requireNonNull(newRoomCategory, "Room category cannot be null");
        Objects.requireNonNull(newPeriod, "Period cannot be null");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(
                        "Booking not found with id: " + bookingId.value()));

        if (!bookingRepository.isRoomAvailable(newRoomCategory, newPeriod)) {
            throw new RoomNotAvailableException(
                    "No room available for category " + newRoomCategory + " in the requested period");
        }

        booking.update(newRoomCategory, newPeriod);
        return bookingRepository.save(booking);
    }
}
