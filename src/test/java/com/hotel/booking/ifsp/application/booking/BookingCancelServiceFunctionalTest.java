package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.*;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("FunctionalTest")
@Tag("TDD")
class BookingCancelServiceFunctionalTest {

    private BookingRepository bookingRepository;
    private BookingCancelService bookingCancelService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        bookingCancelService = new BookingCancelService(bookingRepository);
    }

    @Test
    @DisplayName("Should ensure state transition to CANCELLED is atomic and persisted")
    void shouldProcessCancellationWithCorrectStateTransition() {
        BookingId bookingId = BookingId.generate();
        Period period = new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        Booking booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD, period);
        
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        bookingCancelService.cancelBooking(bookingId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository, times(1)).save(argThat(b -> b.getStatus() == BookingStatus.CANCELLED));
    }

    @Test
    @DisplayName("Should reject cancellation process if booking is already in COMPLETED state")
    void shouldRejectCancellationForCompletedBooking() {
        BookingId bookingId = BookingId.generate();
        Booking booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2)));
        booking.complete();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingCancelService.cancelBooking(bookingId))
                .isInstanceOf(IllegalStateException.class);

        verify(bookingRepository, never()).save(any());
    }

}
