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
}
