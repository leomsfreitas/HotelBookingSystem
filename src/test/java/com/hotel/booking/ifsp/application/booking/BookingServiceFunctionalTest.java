package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.exception.RoomNotAvailableException;
import com.hotel.booking.ifsp.domain.guest.CPF;
import com.hotel.booking.ifsp.domain.guest.Guest;
import com.hotel.booking.ifsp.domain.guest.GuestId;
import com.hotel.booking.ifsp.domain.guest.GuestRepository;
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
@Tag("Functional")
class BookingServiceFunctionalTest {

    private GuestRepository guestRepository;
    private BookingRepository bookingRepository;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        guestRepository = mock(GuestRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingService(guestRepository, bookingRepository);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when check-out equals check-in (boundary: minimum invalid period)")
    void shouldThrowWhenCheckOutEqualsCheckIn() {
        LocalDate date = LocalDate.now().plusDays(1);

        assertThatThrownBy(() -> new Period(date, date))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when check-out is before check-in")
    void shouldThrowWhenCheckOutIsBeforeCheckIn() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = LocalDate.now().plusDays(1);

        assertThatThrownBy(() -> new Period(checkIn, checkOut))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should create valid period when check-out is one day after check-in (boundary: minimum valid period)")
    void shouldAcceptMinimumOneDayPeriod() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Guest guest = new Guest(guestId, "Ana Costa", new CPF("529.982.247-25"));
        RoomCategory category = RoomCategory.STANDARD;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.isRoomAvailable(category, period, null)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> bookingService.registerBooking(guestId, category, period))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw RoomNotAvailableException for DELUXE category when unavailable")
    void shouldThrowWhenDeluxeRoomUnavailable() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Guest guest = new Guest(guestId, "Carlos Lima", new CPF("529.982.247-25"));
        RoomCategory category = RoomCategory.DELUXE;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.isRoomAvailable(category, period, null)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.registerBooking(guestId, category, period))
                .isInstanceOf(RoomNotAvailableException.class);
    }

    @Test
    @DisplayName("Should throw RoomNotAvailableException for SUITE category when unavailable")
    void shouldThrowWhenSuiteRoomUnavailable() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Guest guest = new Guest(guestId, "Maria Souza", new CPF("529.982.247-25"));
        RoomCategory category = RoomCategory.SUITE;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.isRoomAvailable(category, period, null)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.registerBooking(guestId, category, period))
                .isInstanceOf(RoomNotAvailableException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when guestId is null")
    void shouldThrowWhenGuestIdIsNull() {
        RoomCategory category = RoomCategory.STANDARD;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );

        assertThatThrownBy(() -> bookingService.registerBooking(null, category, period))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when roomCategory is null")
    void shouldThrowWhenRoomCategoryIsNull() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Guest guest = new Guest(guestId, "Pedro Alves", new CPF("529.982.247-25"));
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.isRoomAvailable(null, period, null)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatThrownBy(() -> bookingService.registerBooking(guestId, null, period))
                .isInstanceOf(NullPointerException.class);
    }
}
