package com.hotel.booking.ifsp.domain.booking;

import com.hotel.booking.ifsp.domain.guest.GuestId;
import com.hotel.booking.ifsp.domain.room.RoomCategory;

import java.math.BigDecimal;
import java.util.Objects;

public class Booking {

    private final BookingId id;
    private final GuestId guestId;
    private RoomCategory roomCategory;
    private Period period;
    private BigDecimal totalValue;
    private BookingStatus status;

    private Booking(BookingId id, GuestId guestId, RoomCategory roomCategory,
                    Period period, BigDecimal totalValue, BookingStatus status) {
        this.id = id;
        this.guestId = guestId;
        this.roomCategory = roomCategory;
        this.period = period;
        this.totalValue = totalValue;
        this.status = status;
    }

    public static Booking create(GuestId guestId, RoomCategory roomCategory, Period period) {
        Objects.requireNonNull(guestId, "Guest ID cannot be null");
        Objects.requireNonNull(roomCategory, "Room category cannot be null");
        Objects.requireNonNull(period, "Period cannot be null");

        BigDecimal totalValue = roomCategory.getDailyRate()
                .multiply(BigDecimal.valueOf(period.numberOfDays()));

        return new Booking(
                BookingId.generate(),
                guestId,
                roomCategory,
                period,
                totalValue,
                BookingStatus.PENDING
        );
    }

    public void update(RoomCategory newRoomCategory, Period newPeriod) {
        Objects.requireNonNull(newRoomCategory, "Room category cannot be null");
        Objects.requireNonNull(newPeriod, "Period cannot be null");

        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update a cancelled booking");
        }
        if (status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update a completed booking");
        }

        this.roomCategory = newRoomCategory;
        this.period = newPeriod;
        this.totalValue = newRoomCategory.getDailyRate()
                .multiply(BigDecimal.valueOf(newPeriod.numberOfDays()));
    }

    public BookingId getId() { return id; }
    public GuestId getGuestId() { return guestId; }
    public RoomCategory getRoomCategory() { return roomCategory; }
    public Period getPeriod() { return period; }
    public BigDecimal getTotalValue() { return totalValue; }
    public BookingStatus getStatus() { return status; }

    public void cancel() {
        if (status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed booking");
        }
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }
        this.status = BookingStatus.CANCELLED;
    }

    public void complete() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot complete a cancelled booking");
        }
        this.status = BookingStatus.COMPLETED;
    }
}
