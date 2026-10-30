package com.tradeflux.auth_service.service;

import com.tradeflux.auth_service.dto.AuthResponse;
import com.tradeflux.auth_service.dto.LoginRequest;
import com.tradeflux.auth_service.dto.RegisterRequest;
import com.tradeflux.auth_service.entity.RefreshToken;
import com.tradeflux.auth_service.entity.User;
import com.tradeflux.auth_service.enums.Role;
import com.tradeflux.auth_service.exceptions.BadRequestException;
import com.tradeflux.auth_service.kafka.AuthEventProducer;
import com.tradeflux.auth_service.repository.RefreshTokenRepository;
import com.tradeflux.auth_service.repository.UserRepository;
import com.tradeflux.auth_service.security.JwtService;
import com.tradeflux.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AuthEventProducer authEventProducer;


    public AuthResponse register(
            RegisterRequest request
    ) {
        if (
                userRepository.existsByEmail(
                        request.email()
                )
        ) {
            throw new BadRequestException(
                    "Email already exists"
            );
        }
        User user = User.builder()
                .email(request.email())
                .password(
                        passwordEncoder.encode(
                                request.password()
                        )
                )
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String accessToken =
                jwtService.generateToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(
                        user.getEmail()
                );
        saveRefreshToken(
                user,
                refreshToken
        );

        authEventProducer.publishUserRegisteredEvent(
                new UserRegisteredEvent(
                        user.getId(),
                        user.getEmail(),
                        LocalDateTime.now()
                )
        );

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }
    public AuthResponse login(
            LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid credentials"
                        )
                );
        String accessToken =
                jwtService.generateToken(user);

        String refreshToken =
                jwtService.generateRefreshToken(
                        user.getEmail()
                );
        saveRefreshToken(
                user,
                refreshToken
        );
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    public AuthResponse refreshToken(
            String token
    ) {
        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(token)
                        .orElseThrow(() ->

                                new BadRequestException(
                                        "Invalid refresh token"
                                )
                        );
        if (refreshToken.getRevoked()) {

            throw new BadRequestException(
                    "Refresh token revoked"
            );
        }
        if (
                refreshToken.getExpiryDate()
                        .isBefore(LocalDateTime.now())
        ) {
            throw new BadRequestException(
                    "Refresh token expired"
            );
        }
        User user = refreshToken.getUser();
        String newAccessToken =
                jwtService.generateToken(user);
        return new AuthResponse(
                newAccessToken,
                token,
                "Bearer"
        );
    }

    private void saveRefreshToken(

            User user,

            String token
    ) {

        RefreshToken refreshToken =

                refreshTokenRepository
                        .findByUser(user)

                        .orElse(

                                RefreshToken.builder()

                                        .user(user)

                                        .build()
                        );

        refreshToken.setToken(token);

        refreshToken.setRevoked(false);

        refreshToken.setExpiryDate(

                LocalDateTime.now()
                        .plusDays(7)
        );

        refreshTokenRepository.save(
                refreshToken
        );
    }

    public void logout(
            String token
    ) {
        RefreshToken refreshToken =
                refreshTokenRepository

                        .findByToken(token)

                        .orElseThrow(() ->

                                new BadRequestException(
                                        "Invalid refresh token"
                                )
                        );

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(
                refreshToken
        );
    }
}