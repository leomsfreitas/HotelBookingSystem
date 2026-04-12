package com.hotel.booking.ifsp.domain.guest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@Tag("UnitTest")
@Tag("TDD")
class CPFTest {

    @Test
    @DisplayName("Should throw IllegalArgumentException when CPF is invalid")
    void shouldThrowWhenCPFIsInvalid() {
        assertThatThrownBy(() -> new CPF("000.000.000-00"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when CPF has wrong format")
    void shouldThrowWhenCPFHasWrongFormat() {
        assertThatThrownBy(() -> new CPF("123.456.789-00"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should throw NullPointerException when CPF is null")
    void shouldThrowWhenCPFIsNull() {
        assertThatThrownBy(() -> new CPF(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should create CPF successfully when value is valid")
    void shouldCreateCPFSuccessfullyWhenValueIsValid() {
        assertThatCode(() -> new CPF("529.982.247-25"))
                .doesNotThrowAnyException();
    }
}
