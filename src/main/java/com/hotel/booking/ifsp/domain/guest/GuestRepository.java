package com.hotel.booking.ifsp.domain.guest;

import java.util.Optional;

public interface GuestRepository {
    Optional<Guest> findById(GuestId id);
    Guest save(Guest guest);
}
