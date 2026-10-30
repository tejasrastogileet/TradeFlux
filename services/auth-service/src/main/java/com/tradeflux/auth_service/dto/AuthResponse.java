package com.tradeflux.auth_service.dto;

public record AuthResponse(

        String accessToken,
        String refreshToken,

        String tokenType
) {
}
