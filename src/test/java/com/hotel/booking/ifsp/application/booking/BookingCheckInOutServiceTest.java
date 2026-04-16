package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.*;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;
import com.hotel.booking.ifsp.domain.guest.GuestId;
import com.hotel.booking.ifsp.domain.room.RoomCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        service = new BookingCheckInOutService(bookingRepository);
        booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(LocalDate.now(), LocalDate.now().plusDays(2)));
    }

    @Test
    @DisplayName("Should process check-in successfully")
    void shouldProcessCheckIn() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        service.checkIn(booking.getId());

        assertThat(booking.getStatus().name()).isEqualTo("CHECKED_IN");
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Should process check-out successfully")
    void shouldProcessCheckOut() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        service.checkIn(booking.getId());

        service.checkOut(booking.getId());

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        verify(bookingRepository, times(2)).save(booking);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to check-out without prior check-in")
    void shouldThrowIllegalStateExceptionWhenCheckingOutWithoutCheckIn() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.checkOut(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot check-out a booking that has not been checked-in");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should ensure check-in is only possible for PENDING bookings")
    void shouldOnlyAllowCheckInForPendingBookings() {
        booking.cancel();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.checkIn(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can only check-in a pending booking");

        GuestId guestId = new GuestId(UUID.randomUUID());
        Booking bookingCompleted = Booking.create(guestId, RoomCategory.STANDARD,
                new Period(LocalDate.now(), LocalDate.now().plusDays(2)));
        bookingCompleted.checkIn();
        bookingCompleted.checkOut();
        when(bookingRepository.findById(bookingCompleted.getId())).thenReturn(Optional.of(bookingCompleted));

        assertThatThrownBy(() -> service.checkIn(bookingCompleted.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can only check-in a pending booking");
    }

    @Test
    @DisplayName("Should throw BookingNotFoundException when trying to check-in a non-existent booking")
    void shouldThrowExceptionWhenCheckInBookingNotFound() {
        BookingId unknownId = BookingId.generate();
        when(bookingRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.checkIn(unknownId))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("Booking not found with id: " + unknownId.value());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NullPointerException when booking ID is null during check-in")
    void shouldThrowNullPointerExceptionWhenCheckInIdIsNull() {
        assertThatThrownBy(() -> service.checkIn(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Booking ID cannot be null");

        verify(bookingRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to check-in a CANCELLED booking")
    void shouldThrowExceptionWhenCheckInCancelledBooking() {
        booking.cancel();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.checkIn(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can only check-in a pending booking");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to perform check-in on an already CHECKED_IN booking")
    void shouldThrowIllegalStateExceptionOnDuplicateCheckIn() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        service.checkIn(booking.getId());

        assertThatThrownBy(() -> service.checkIn(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Can only check-in a pending booking");

        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should ensure check-out is only possible for CHECKED_IN bookings")
    void shouldOnlyAllowCheckOutForCheckedInBookings() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.checkOut(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot check-out a booking that has not been checked-in");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should process check-out correctly for valid CHECKED_IN reservation")
    void shouldProcessCheckOutForCheckedInBooking() {
        booking.checkIn();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        service.checkOut(booking.getId());

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to check-out a CANCELLED booking")
    void shouldThrowExceptionWhenCheckOutCancelledBooking() {booking.cancel();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.checkOut(booking.getId()))
                .isInstanceOf(IllegalStateException.class);

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to perform check-out on an already COMPLETED booking")
    void shouldThrowIllegalStateExceptionOnDuplicateCheckOut() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        service.checkIn(booking.getId());
        service.checkOut(booking.getId());

        assertThatThrownBy(() -> service.checkOut(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot check-out a booking that has not been checked-in");
        verify(bookingRepository, times(2)).save(any());
    }


    @Test
    @DisplayName("Should ensure status is exactly COMPLETED after successful check-out")
    void shouldUpdateStatusToCompletedEnum() {
        booking.checkIn();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CHECKED_IN);

        service.checkOut(booking.getId());

        assertThat(booking.getStatus()).isSameAs(BookingStatus.COMPLETED);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when checking in before scheduled date")
    void shouldThrowExceptionWhenCheckingInBeforeScheduledDate() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        Booking bookingFuture = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(futureDate, futureDate.plusDays(3)));
        when(bookingRepository.findById(bookingFuture.getId())).thenReturn(Optional.of(bookingFuture));

        assertThatThrownBy(() -> service.checkIn(bookingFuture.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when checking out on an invalid date")
    void shouldThrowExceptionWhenCheckingOutOnInvalidDate() {
        LocalDate futureDate = LocalDate.now().plusDays(2);
        Booking bookingFuture = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(futureDate, futureDate.plusDays(3)));

        bookingFuture.checkIn();
        when(bookingRepository.findById(bookingFuture.getId())).thenReturn(Optional.of(bookingFuture));

        assertThatThrownBy(() -> service.checkOut(bookingFuture.getId()))
                .isInstanceOf(IllegalStateException.class);
    }
}