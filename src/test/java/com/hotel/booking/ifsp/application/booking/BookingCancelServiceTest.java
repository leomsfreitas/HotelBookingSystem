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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

}
