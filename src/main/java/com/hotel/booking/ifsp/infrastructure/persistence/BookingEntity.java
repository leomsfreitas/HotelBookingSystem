package com.hotel.booking.ifsp.infrastructure.persistence;

import com.hotel.booking.ifsp.domain.booking.BookingStatus;
import com.hotel.booking.ifsp.domain.room.RoomCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking")
public class BookingEntity {

    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Column(nullable = false)
    private UUID id;

    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "guest_id", nullable = false)
    private UUID guestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_category", nullable = false)
    private RoomCategory roomCategory;

    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;

    @Column(name = "total_value", nullable = false)
    private BigDecimal totalValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;
}
