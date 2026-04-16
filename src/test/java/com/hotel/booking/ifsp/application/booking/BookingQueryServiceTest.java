package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.booking.BookingStatus;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;
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
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
class BookingQueryServiceTest {

    private BookingRepository bookingRepository;
    private GuestRepository guestRepository;
    private BookingQueryService bookingQueryService;

    private GuestId guestId;
    private Guest guest;
    private Period period;
    private Booking booking;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        guestRepository = mock(GuestRepository.class);
        bookingQueryService = new BookingQueryService(bookingRepository, guestRepository);

        guestId = new GuestId(UUID.randomUUID());
        guest = new Guest(guestId, "João Silva", new CPF("529.982.247-25"));
        period = new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(5));
        booking = Booking.create(guestId, RoomCategory.STANDARD, period);

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));
    }

    @Test
    @DisplayName("Should return booking details when booking exists")
    void shouldReturnBookingDetailsWhenBookingExists() {
        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result).isNotNull();
        assertThat(result.bookingId()).isEqualTo(booking.getId());
    }

    @Test
    @DisplayName("Should throw BookingNotFoundException when booking does not exist")
    void shouldThrowBookingNotFoundExceptionWhenBookingDoesNotExist() {
        BookingId unknownId = BookingId.generate();
        when(bookingRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingQueryService.findBooking(unknownId))
                .isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when booking ID is null")
    void shouldThrowNullPointerExceptionWhenBookingIdIsNull() {
        assertThatThrownBy(() -> bookingQueryService.findBooking(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should return guest name when booking is queried")
    void shouldReturnGuestNameWhenBookingIsQueried() {
        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result.guestName()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Should return guest CPF when booking is queried")
    void shouldReturnGuestCpfWhenBookingIsQueried() {
        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result.guestCpf()).isEqualTo("529.982.247-25");
    }

    @Test
    @DisplayName("Should return room category when booking is queried")
    void shouldReturnRoomCategoryWhenBookingIsQueried() {
        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result.roomCategory()).isEqualTo(RoomCategory.STANDARD);
    }

    @Test
    @DisplayName("Should return period with check-in and check-out when booking is queried")
    void shouldReturnPeriodWhenBookingIsQueried() {
        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result.period().checkIn()).isEqualTo(period.checkIn());
        assertThat(result.period().checkOut()).isEqualTo(period.checkOut());
    }

    @Test
    @DisplayName("Should return current booking status when booking is queried")
    void shouldReturnBookingStatusWhenBookingIsQueried() {
        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result.status()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    @DisplayName("Should return CANCELLED status when cancelled booking is queried")
    void shouldReturnCancelledStatusWhenCancelledBookingIsQueried() {
        booking.cancel();

        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result.status()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should return COMPLETED status when completed booking is queried")
    void shouldReturnCompletedStatusWhenCompletedBookingIsQueried() {
        booking.checkIn();
        booking.complete();

        BookingDetails result = bookingQueryService.findBooking(booking.getId());

        assertThat(result.status()).isEqualTo(BookingStatus.COMPLETED);
    }

}
