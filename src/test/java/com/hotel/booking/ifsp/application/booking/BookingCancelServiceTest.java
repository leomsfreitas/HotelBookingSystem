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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;


@Tag("UnitTest")
@Tag("TDD")
class BookingCancelServiceTest {

    private BookingRepository bookingRepository;
    private BookingCancelService bookingCancelService;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        bookingCancelService = new BookingCancelService(bookingRepository);

        Period period = new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD, period);
    }

    @Test
    @DisplayName("Should cancel booking successfully when it exists and is pending")
    void shouldCancelBookingSuccessfully() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        bookingCancelService.cancelBooking(booking.getId());

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
    }

    @Test
    @DisplayName("Should throw BookingNotFoundException when cancelling a non-existent booking")
    void shouldThrowExceptionWhenBookingNotFound() {
        BookingId unknownId = BookingId.generate();
        when(bookingRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingCancelService.cancelBooking(unknownId))
                .isInstanceOf(BookingNotFoundException.class);

        verify(bookingRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw BookingNotFoundException with correct message when booking ID does not exist")
        void shouldThrowExceptionWithDetailsWhenBookingNotFound() {
        BookingId unknownId = BookingId.generate();
        when(bookingRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingCancelService.cancelBooking(unknownId))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessageContaining("Booking not found with id: " + unknownId.value());

        verify(bookingRepository, times(1)).findById(unknownId);
        verify(bookingRepository, never()).save(any());
        }

    @Test
    @DisplayName("Should throw NullPointerException when booking ID is null during cancellation")
    void shouldThrowNullPointerExceptionWhenBookingIdIsNull() {
        assertThatThrownBy(() -> bookingCancelService.cancelBooking(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Booking ID cannot be null");

        verify(bookingRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to cancel an already cancelled booking through the service")
    void shouldThrowIllegalStateExceptionOnDuplicateCancellation() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        bookingCancelService.cancelBooking(booking.getId());

        reset(bookingRepository);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingCancelService.cancelBooking(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking is already cancelled");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to cancel a completed booking through the service")
    void shouldThrowIllegalStateExceptionOnCancellingCompletedBooking() {
        booking.complete();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingCancelService.cancelBooking(booking.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot cancel a completed booking");

        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should ensure all booking data is preserved when saved as CANCELLED for history")
    void shouldPreserveDataWhenPersistingCancelledBooking() {
        BookingId id = booking.getId();
        GuestId originalGuestId = booking.getGuestId();
        RoomCategory originalCategory = booking.getRoomCategory();
        Period originalPeriod = booking.getPeriod();

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        bookingCancelService.cancelBooking(id);

        verify(bookingRepository).save(argThat(savedBooking ->
            savedBooking.getId().equals(id) &&
            savedBooking.getStatus() == BookingStatus.CANCELLED &&
            savedBooking.getGuestId().equals(originalGuestId) &&
            savedBooking.getRoomCategory() == originalCategory &&
            savedBooking.getPeriod().equals(originalPeriod)
        ));
    }

    @Test
    @DisplayName("Should update status specifically to CANCELLED enum value")
    void shouldUpdateStatusToCancelledEnum() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);

        bookingCancelService.cancelBooking(booking.getId());

        assertThat(booking.getStatus()).isSameAs(BookingStatus.CANCELLED);
    }
}

