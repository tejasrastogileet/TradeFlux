package com.tradeflux.api_gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;


    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtSecretKey.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }

    private Claims extractClaims(
            String token
    ) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(
            String token
    ) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getUserIdFromToken(
            String token
    ) {
        Claims claims =
                extractClaims(token);
        Object userId =
                claims.get("userId");
        return userId != null
                ? userId.toString()
                : null;
    }

    public String getEmailFromToken(
            String token
    ) {
        Claims claims =
                extractClaims(token);
        Object email =
                claims.get("email");
        return email != null
                ? email.toString()
                : null;
    }


    public String getRoleFromToken(
            String token
    ) {

        Claims claims =
                extractClaims(token);

        Object role =
                claims.get("role");

        return role != null
                ? role.toString()
                : null;
    }
}