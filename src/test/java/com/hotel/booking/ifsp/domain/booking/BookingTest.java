package com.hotel.booking.ifsp.domain.booking;

import com.hotel.booking.ifsp.domain.guest.GuestId;
import com.hotel.booking.ifsp.domain.room.RoomCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Tag("UnitTest")
@Tag("TDD")
class BookingTest {

    @Test
    @DisplayName("Should calculate total value as daily rate multiplied by number of days")
    void shouldCalculateTotalValueAsProductOfDailyRateAndNumberOfDays() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(6)
        );

        Booking booking = Booking.create(guestId, RoomCategory.STANDARD, period);

        assertThat(booking.getTotalValue()).isEqualByComparingTo(new BigDecimal("750.00"));
    }

    @Test
    @DisplayName("Should calculate total value correctly for DELUXE category")
    void shouldCalculateTotalValueForDeluxeCategory() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4)
        );

        Booking booking = Booking.create(guestId, RoomCategory.DELUXE, period);

        assertThat(booking.getTotalValue()).isEqualByComparingTo(new BigDecimal("750.00"));
    }

    @Test
    @DisplayName("Should set initial booking status as PENDING when booking is created")
    void shouldSetInitialStatusAsPending() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Period period = new Period(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5)
        );

        Booking booking = Booking.create(guestId, RoomCategory.STANDARD, period);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    @DisplayName("Should recalculate total value when room category and period are updated")
    void shouldRecalculateValueOnUpdate() {
        GuestId guestId = new GuestId(UUID.randomUUID());
        Period oldPeriod = new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        Booking booking = Booking.create(guestId, RoomCategory.STANDARD, oldPeriod);
        BigDecimal initialValue = booking.getTotalValue();

        Period newPeriod = new Period(LocalDate.now().plusDays(5), LocalDate.now().plusDays(8));
        booking.update(RoomCategory.DELUXE, newPeriod);

        assertThat(booking.getTotalValue()).isNotEqualTo(initialValue);
        BigDecimal expectedValue = RoomCategory.DELUXE.getDailyRate()
                .multiply(BigDecimal.valueOf(newPeriod.numberOfDays()));
        assertThat(booking.getTotalValue()).isEqualByComparingTo(expectedValue);
    }

    @Test
    @DisplayName("Should change status to CANCELLED when cancelling a pending booking")
    void shouldCancelPendingBooking() {
        Booking booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD, 
                new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));
        
        booking.cancel();
        
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when cancelling an already cancelled booking")
    void shouldThrowExceptionWhenCancellingAlreadyCancelled() {
        Booking booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));
        booking.cancel();

        assertThatThrownBy(booking::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Booking is already cancelled");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when cancelling a completed booking")
    void shouldThrowExceptionWhenCancellingCompleted() {
        Booking booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));
        booking.checkIn();
        booking.complete();

        assertThatThrownBy(booking::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot cancel a completed booking");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to update a cancelled booking")
    void shouldThrowExceptionWhenUpdatingCancelledBooking() {
        Booking booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD, 
                new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));
        booking.cancel();
        
        Period newPeriod = new Period(LocalDate.now().plusDays(5), LocalDate.now().plusDays(8));
        
        assertThatThrownBy(() -> booking.update(RoomCategory.DELUXE, newPeriod))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update a cancelled booking");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to complete (check-in/finish) a cancelled booking")
    void shouldThrowExceptionWhenCompletingCancelledBooking() {
        Booking booking = Booking.create(new GuestId(java.util.UUID.randomUUID()), RoomCategory.STANDARD, 
                new Period(java.time.LocalDate.now().plusDays(1), java.time.LocalDate.now().plusDays(3)));
        booking.cancel();
        
        assertThatThrownBy(booking::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot complete a cancelled booking");
    }

    @Test
    @DisplayName("Should preserve all original booking data (Guest, Category, Period, Value) after cancellation")
    void shouldPreserveOriginalDataAfterCancellation() {
        GuestId guestId = new GuestId(java.util.UUID.randomUUID());
        RoomCategory category = RoomCategory.DELUXE;
        Period period = new Period(java.time.LocalDate.now().plusDays(1), java.time.LocalDate.now().plusDays(5));
        Booking booking = Booking.create(guestId, category, period);
        java.math.BigDecimal originalValue = booking.getTotalValue();

        booking.cancel();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(booking.getGuestId()).isEqualTo(guestId);
        assertThat(booking.getRoomCategory()).isEqualTo(category);
        assertThat(booking.getPeriod()).isEqualTo(period);
        assertThat(booking.getTotalValue()).isEqualByComparingTo(originalValue);
    }

    @Test
    @DisplayName("Should transition status to CHECKED_IN when performing check-in")
    void shouldTransitionToCheckedIn() {
        Booking booking = Booking.create(new GuestId(java.util.UUID.randomUUID()), RoomCategory.STANDARD, 
                new Period(java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(2)));
        
        booking.checkIn();
        
        assertThat(booking.getStatus().name()).isEqualTo("CHECKED_IN");
    }

    @Test
    @DisplayName("Should transition status to COMPLETED when performing check-out after check-in")
    void shouldTransitionToCompletedOnCheckOut() {
        Booking booking = Booking.create(new GuestId(java.util.UUID.randomUUID()), RoomCategory.STANDARD, 
                new Period(java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(2)));
        booking.checkIn(); 
        booking.checkOut();
        
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to check-out without check-in")
    void shouldThrowExceptionOnCheckOutWithoutCheckIn() {
        Booking booking = Booking.create(new GuestId(java.util.UUID.randomUUID()), RoomCategory.STANDARD, 
                new Period(java.time.LocalDate.now(), java.time.LocalDate.now().plusDays(2)));
        
        assertThatThrownBy(booking::checkOut)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot check-out a booking that has not been checked-in");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to complete (check-out) a PENDING booking directly")
    void shouldThrowExceptionWhenCompletingPendingBooking() {
        Booking booking = Booking.create(new GuestId(UUID.randomUUID()), RoomCategory.STANDARD,
                new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3)));

        assertThatThrownBy(booking::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot complete a booking that has not been checked-in");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when trying to update a checked-in booking")
    void shouldThrowExceptionWhenUpdatingCheckedInBooking() {
        Booking booking = Booking.create(new GuestId(java.util.UUID.randomUUID()), RoomCategory.STANDARD, 
                new Period(java.time.LocalDate.now().plusDays(1), java.time.LocalDate.now().plusDays(3)));
        booking.checkIn();
        
        Period newPeriod = new Period(java.time.LocalDate.now().plusDays(5), java.time.LocalDate.now().plusDays(8));
        
        assertThatThrownBy(() -> booking.update(RoomCategory.DELUXE, newPeriod))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot update a booking that is already checked-in");
    }
}
