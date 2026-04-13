package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;
import com.hotel.booking.ifsp.domain.guest.CPF;
import com.hotel.booking.ifsp.domain.guest.Guest;
import com.hotel.booking.ifsp.domain.guest.GuestId;
import com.hotel.booking.ifsp.domain.room.RoomCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
class BookingUpdateServiceTest {

    private BookingRepository bookingRepository;
    private BookingUpdateService bookingUpdateService;

    private GuestId guestId;
    private Booking booking;
    private Period originalPeriod;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        bookingUpdateService = new BookingUpdateService(bookingRepository);

        guestId = new GuestId(UUID.randomUUID());
        originalPeriod = new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        booking = Booking.create(guestId, RoomCategory.STANDARD, originalPeriod);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    @DisplayName("Should update booking successfully when booking exists and new data is valid")
    void shouldUpdateBookingSuccessfully() {
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

        Booking result = bookingUpdateService.updateBooking(booking.getId(), RoomCategory.DELUXE, newPeriod);

        assertThat(result.getRoomCategory()).isEqualTo(RoomCategory.DELUXE);
        assertThat(result.getPeriod()).isEqualTo(newPeriod);
        verify(bookingRepository).save(any(Booking.class));
    }
}
