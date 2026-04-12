package com.hotel.booking.ifsp.domain.guest;

import java.util.Objects;

public class Guest {

    private final GuestId id;
    private final String name;
    private final CPF cpf;

    public Guest(GuestId id, String name, CPF cpf) {
        Objects.requireNonNull(id, "Guest ID cannot be null");
        Objects.requireNonNull(name, "Guest name cannot be null");
        Objects.requireNonNull(cpf, "CPF cannot be null");
        if (name.isBlank()) throw new IllegalArgumentException("Guest name cannot be blank");
        this.id = id;
        this.name = name;
        this.cpf = cpf;
    }

    public GuestId getId() { return id; }
    public String getName() { return name; }
    public CPF getCpf() { return cpf; }
}
