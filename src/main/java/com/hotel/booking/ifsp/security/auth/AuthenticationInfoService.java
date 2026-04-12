package com.hotel.booking.ifsp.security.auth;

import com.hotel.booking.ifsp.security.user.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationInfoService {

    public UUID getAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated())
            throw new IllegalStateException("Unauthorized user request.");
        var applicationUser = (User) authentication.getPrincipal();
        return applicationUser.getId();
    }
}
