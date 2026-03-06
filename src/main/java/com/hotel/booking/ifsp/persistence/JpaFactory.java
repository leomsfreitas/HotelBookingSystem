package com.hotel.booking.ifsp.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaFactory {
    private static EntityManagerFactory FACTORY;

    private JpaFactory() {

    }

    public static EntityManagerFactory getFactory() {
        if (FACTORY == null)
            FACTORY = Persistence.createEntityManagerFactory("HotelBookingSystem");

        return FACTORY;
    }

    public static EntityManager getEntityManager() {
        return getFactory().createEntityManager();
    }

}
