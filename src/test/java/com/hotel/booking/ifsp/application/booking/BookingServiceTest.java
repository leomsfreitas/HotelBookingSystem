package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.exception.GuestNotFoundException;
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
@Tag("TDD")
class BookingServiceTest {

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
    @DisplayName("Should register booking successfully when guest exists and room is available")
    void shouldRegisterBookingSuccessfully() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Guest guest = new Guest(guestId, "João Silva", new CPF("529.982.247-25"));
        RoomCategory category = RoomCategory.STANDARD;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.isRoomAvailable(category, period, null)).thenReturn(true);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        BookingId result = bookingService.registerBooking(guestId, category, period);

        assertThat(result).isNotNull();
        assertThat(result.value()).isNotNull();
        verify(bookingRepository).isRoomAvailable(category, period, null);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw GuestNotFoundException when guest does not exist")
    void shouldThrowGuestNotFoundExceptionWhenGuestDoesNotExist() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        RoomCategory category = RoomCategory.STANDARD;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        when(guestRepository.findById(guestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.registerBooking(guestId, category, period))
                .isInstanceOf(GuestNotFoundException.class);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw NullPointerException when guest ID is null")
    void shouldThrowNullPointerExceptionWhenGuestIdIsNull() {
        RoomCategory category = RoomCategory.STANDARD;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );

        assertThatThrownBy(() -> bookingService.registerBooking(null, category, period))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when room category is null")
    void shouldThrowNullPointerExceptionWhenRoomCategoryIsNull() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );

        assertThatThrownBy(() -> bookingService.registerBooking(guestId, null, period))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw RoomNotAvailableException when no room is available for the requested period")
    void shouldThrowRoomNotAvailableExceptionWhenRoomIsUnavailable() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Guest guest = new Guest(guestId, "João Silva", new CPF("529.982.247-25"));
        RoomCategory category = RoomCategory.STANDARD;
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );
        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
        when(bookingRepository.isRoomAvailable(category, period, null)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.registerBooking(guestId, category, period))
                .isInstanceOf(RoomNotAvailableException.class);
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
