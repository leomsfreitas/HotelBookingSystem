package com.hotel.booking.ifsp.domain.booking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@Tag("UnitTest")
@Tag("TDD")
class PeriodTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when check-out equals check-in")
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
    @DisplayName("Should create period successfully when check-out is after check-in")
    void shouldCreatePeriodSuccessfullyWhenDatesAreValid() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = LocalDate.now().plusDays(2);

        assertThatCode(() -> new Period(checkIn, checkOut))
                .doesNotThrowAnyException();
    }
}
