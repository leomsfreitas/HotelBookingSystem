package com.hotel.booking.ifsp.domain.booking;

import com.hotel.booking.ifsp.domain.room.RoomCategory;

import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(BookingId id);
    boolean isRoomAvailable(RoomCategory roomCategory, Period period, BookingId excludeBookingId);
}
