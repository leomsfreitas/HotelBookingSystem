package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingStatus;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.room.RoomCategory;

import java.math.BigDecimal;

public record BookingDetails(
        BookingId bookingId,
        String guestName,
        String guestCpf,
        RoomCategory roomCategory,
        Period period,
        BigDecimal totalValue,
        BookingStatus status
) {}
