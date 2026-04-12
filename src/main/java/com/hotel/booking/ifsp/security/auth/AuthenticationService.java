package com.hotel.booking.ifsp.security.auth;

import com.hotel.booking.ifsp.exception.EntityAlreadyExistsException;
import com.hotel.booking.ifsp.security.config.JwtService;
import com.hotel.booking.ifsp.security.user.JpaUserRepository;
import com.hotel.booking.ifsp.security.user.Role;
import com.hotel.booking.ifsp.security.user.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public RegisterUserResponse register(RegisterUserRequest request) {
        userRepository.findByEmail(request.email()).ifPresent(unused -> {
            throw new EntityAlreadyExistsException("Email already registered: " + request.email());
        });

        String encryptedPassword = passwordEncoder.encode(request.password());

        final UUID id = UUID.randomUUID();
        final User user = User.builder()
                .id(id)
                .name(request.name())
                .lastname(request.lastname())
                .email(request.email())
                .password(encryptedPassword)
                .role(Role.USER)
                .build();

        userRepository.save(user);
        return new RegisterUserResponse(id);
    }

    public AuthResponse authenticate(AuthRequest request) {
        final var authentication = new UsernamePasswordAuthenticationToken(
                request.username(), request.password());
        authenticationManager.authenticate(authentication);

        final User user = userRepository.findByEmail(request.username()).orElseThrow();
        final String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
