package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.*;
import com.hotel.booking.ifsp.domain.guest.GuestId;
import com.hotel.booking.ifsp.domain.room.RoomCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
class BookingCheckInOutServiceTest {

    private BookingRepository bookingRepository;
    private BookingCheckInOutService service;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(LocalDate.now(), LocalDate.now().plusDays(2)));
    }

    @Test
    @DisplayName("Should process check-in successfully")
    void shouldProcessCheckIn() {
    }
}