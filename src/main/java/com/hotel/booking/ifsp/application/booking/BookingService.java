package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.exception.GuestNotFoundException;
import com.hotel.booking.ifsp.domain.exception.RoomNotAvailableException;
import com.hotel.booking.ifsp.domain.guest.GuestId;
import com.hotel.booking.ifsp.domain.guest.GuestRepository;
import com.hotel.booking.ifsp.domain.room.RoomCategory;

public class BookingService {

    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;

    public BookingService(GuestRepository guestRepository, BookingRepository bookingRepository) {
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }

    public BookingId registerBooking(GuestId guestId, RoomCategory roomCategory, Period period) {
        guestRepository.findById(guestId)
                .orElseThrow(() -> new GuestNotFoundException(
                        "Guest not found with id: " + guestId.value()));

        if (!bookingRepository.isRoomAvailable(roomCategory, period)) {
            throw new RoomNotAvailableException(
                    "No room available for category " + roomCategory + " in the requested period");
        }

        Booking booking = Booking.create(guestId, roomCategory, period);
        Booking saved = bookingRepository.save(booking);
        return saved.getId();
    }
}
