package com.tradeflux.api_gateway.security;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
@Component
public class AuthenticationFilter extends
        AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtService jwtService;
    private final RouteValidator routeValidator;

    public AuthenticationFilter(JwtService jwtService, RouteValidator routeValidator) {
        super(Config.class);
        this.jwtService = jwtService;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(
            Config config
    ) {

        return (exchange, chain) -> {

            String path =
                    exchange.getRequest()
                            .getURI()
                            .getPath();

            if (!routeValidator.isSecured.test(path)) {
                return chain.filter(exchange);
            }
            String authHeader =
                    exchange.getRequest()
                            .getHeaders()
                            .getFirst(
                                    HttpHeaders.AUTHORIZATION
                            );

            if (
                    authHeader == null
                            ||
                            !authHeader.startsWith("Bearer ")
            ) {
                log.error(
                        "Missing Authorization header"
                );
                exchange.getResponse()
                        .setStatusCode(
                                HttpStatus.UNAUTHORIZED
                        );

                return exchange.getResponse()
                        .setComplete();
            }
            String token =
                    authHeader.substring(7);

            try {
                if (
                        !jwtService.validateToken(token)
                ) {

                    exchange.getResponse()
                            .setStatusCode(
                                    HttpStatus.UNAUTHORIZED
                            );

                    return exchange.getResponse()
                            .setComplete();
                }
                String userId =
                        jwtService.getUserIdFromToken(
                                token
                        );

                String email =
                        jwtService.getEmailFromToken(
                                token
                        );

                String role =
                        jwtService.getRoleFromToken(
                                token
                        );

                ServerWebExchange modifiedExchange =

                        exchange.mutate()

                                .request(

                                        exchange.getRequest()

                                                .mutate()

                                                .header(
                                                        "X-User-Id",
                                                        userId
                                                )

                                                .header(
                                                        "X-User-Email",
                                                        email
                                                )

                                                .header(
                                                        "X-User-Role",
                                                        role
                                                )

                                                .build()
                                )

                                .build();

                log.info(
                        "Authenticated user: {}",
                        email
                );

                return chain.filter(modifiedExchange);
            } catch (JwtException ex) {

                log.error(
                        "JWT validation failed: {}",
                        ex.getMessage()
                );

                exchange.getResponse()
                        .setStatusCode(
                                HttpStatus.UNAUTHORIZED
                        );

                return exchange.getResponse()
                        .setComplete();
            }
        };
    }

    public static class Config {
    }
}