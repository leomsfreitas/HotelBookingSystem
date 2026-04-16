package com.hotel.booking.ifsp.application.booking;

import com.hotel.booking.ifsp.domain.booking.Booking;
import com.hotel.booking.ifsp.domain.booking.BookingId;
import com.hotel.booking.ifsp.domain.booking.BookingRepository;
import com.hotel.booking.ifsp.domain.booking.Period;
import com.hotel.booking.ifsp.domain.exception.BookingNotFoundException;
import com.hotel.booking.ifsp.domain.exception.RoomNotAvailableException;
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
        when(bookingRepository.isRoomAvailable(any(RoomCategory.class), any(Period.class), any())).thenReturn(true);

        Booking result = bookingUpdateService.updateBooking(booking.getId(), RoomCategory.DELUXE, newPeriod);

        assertThat(result.getRoomCategory()).isEqualTo(RoomCategory.DELUXE);
        assertThat(result.getPeriod()).isEqualTo(newPeriod);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw RoomNotAvailableException when updating to an unavailable period/category")
    void shouldThrowRoomNotAvailableExceptionWhenUpdatingToUnavailablePeriod() {
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));
        when(bookingRepository.isRoomAvailable(RoomCategory.DELUXE, newPeriod, booking.getId())).thenReturn(false);

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(booking.getId(), RoomCategory.DELUXE, newPeriod))
                .isInstanceOf(RoomNotAvailableException.class);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw BookingNotFoundException when booking does not exist")
    void shouldThrowBookingNotFoundExceptionWhenBookingDoesNotExist() {
        BookingId unknownId = BookingId.generate();
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));
        when(bookingRepository.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(unknownId, RoomCategory.STANDARD, newPeriod))
                .isInstanceOf(BookingNotFoundException.class);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw NullPointerException when booking ID is null")
    void shouldThrowNullPointerExceptionWhenBookingIdIsNull() {
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(null, RoomCategory.STANDARD, newPeriod))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when new period is null")
    void shouldThrowNullPointerExceptionWhenNewPeriodIsNull() {
        assertThatThrownBy(() -> bookingUpdateService.updateBooking(booking.getId(), RoomCategory.STANDARD, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when new room category is null")
    void shouldThrowNullPointerExceptionWhenNewRoomCategoryIsNull() {
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(booking.getId(), null, newPeriod))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw RoomNotAvailableException when trying to update a cancelled booking")
    void shouldThrowIllegalStateExceptionWhenUpdatingCancelledBooking() {
        booking.cancel();
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(booking.getId(), RoomCategory.DELUXE, newPeriod))
                .isInstanceOf(RoomNotAvailableException.class);
    }

    @Test
    @DisplayName("Should throw RoomNotAvailableException when trying to update a completed booking")
    void shouldThrowIllegalStateExceptionWhenUpdatingCompletedBooking() {
        booking.checkIn();
        booking.complete();
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(booking.getId(), RoomCategory.DELUXE, newPeriod))
                .isInstanceOf(RoomNotAvailableException.class);
    }

    @Test
    @DisplayName("Should verify room availability before updating booking (Success case)")
    void shouldVerifyAvailabilityOnUpdateSuccess() {
        Period newPeriod = new Period(LocalDate.now().plusDays(20), LocalDate.now().plusDays(25));
        when(bookingRepository.isRoomAvailable(RoomCategory.STANDARD, newPeriod, booking.getId())).thenReturn(true);

        bookingUpdateService.updateBooking(booking.getId(), RoomCategory.STANDARD, newPeriod);

        verify(bookingRepository).isRoomAvailable(RoomCategory.STANDARD, newPeriod, booking.getId());
    }

    @Test
    @DisplayName("Should throw RoomNotAvailableException when there is a date conflict")
    void shouldThrowExceptionWhenUpdateHasConflict() {
        Period conflictingPeriod = new Period(LocalDate.now().plusDays(2), LocalDate.now().plusDays(6));
        when(bookingRepository.isRoomAvailable(RoomCategory.STANDARD, conflictingPeriod, booking.getId())).thenReturn(false);

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(booking.getId(), RoomCategory.STANDARD, conflictingPeriod))
                .isInstanceOf(RoomNotAvailableException.class);

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should ensure the updated booking with new price is saved in the repository")
    void shouldSaveBookingWithRecalculatedPrice() {
        Period newPeriod = new Period(LocalDate.now().plusDays(5), LocalDate.now().plusDays(10)); // 5 dias
        when(bookingRepository.isRoomAvailable(RoomCategory.DELUXE, newPeriod, booking.getId())).thenReturn(true);

        Booking updatedBooking = bookingUpdateService.updateBooking(booking.getId(), RoomCategory.DELUXE, newPeriod);

        java.math.BigDecimal expectedPrice = RoomCategory.DELUXE.getDailyRate().multiply(java.math.BigDecimal.valueOf(5));
        
        assertThat(updatedBooking.getTotalValue()).isEqualByComparingTo(expectedPrice);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when update period has check-out before check-in")
    void shouldThrowIllegalArgumentExceptionWhenPeriodIsInvalid() {
        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate invalidCheckOut = LocalDate.now().plusDays(2);

        assertThatThrownBy(() -> new Period(checkIn, invalidCheckOut))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Check-out must be after check-in");
    }

    @Test
    @DisplayName("Should not allow updating the Guest ID of an existing booking")
    void shouldNotAllowUpdatingGuestId() {
        GuestId originalGuestId = booking.getGuestId();
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));
        when(bookingRepository.isRoomAvailable(any(), any(), any())).thenReturn(true);

        Booking updated = bookingUpdateService.updateBooking(booking.getId(), RoomCategory.DELUXE, newPeriod);

        assertThat(updated.getGuestId()).isEqualTo(originalGuestId);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when attempting to update to a past period")
    void shouldThrowExceptionWhenUpdatingToPastPeriod() {
        Period pastPeriod = new Period(LocalDate.now().minusDays(5), LocalDate.now().minusDays(2));
        when(bookingRepository.isRoomAvailable(RoomCategory.STANDARD, pastPeriod, booking.getId())).thenReturn(true);

        assertThatThrownBy(() -> bookingUpdateService.updateBooking(booking.getId(), RoomCategory.STANDARD, pastPeriod))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update booking to a past period");
    }

    @Test
    @DisplayName("Should not conflict with itself when updating booking")
    void shouldNotConflictWithItselfWhenUpdatingBooking() {
        Period newPeriod = new Period(LocalDate.now().plusDays(10), LocalDate.now().plusDays(15));

        when(bookingRepository.isRoomAvailable(RoomCategory.STANDARD, newPeriod, booking.getId()))
                .thenReturn(true);

        bookingUpdateService.updateBooking(booking.getId(), RoomCategory.STANDARD, newPeriod);

        verify(bookingRepository).isRoomAvailable(RoomCategory.STANDARD, newPeriod, booking.getId());
        verify(bookingRepository).save(any(Booking.class));
    }
}
