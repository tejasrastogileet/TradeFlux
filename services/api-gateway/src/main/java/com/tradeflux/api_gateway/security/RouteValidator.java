package com.tradeflux.api_gateway.security;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {


    public static final List<String> openApiEndpoints = List.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/actuator"
    );

    public Predicate<String> isSecured =

            uri -> openApiEndpoints
                    .stream()
                    .noneMatch(uri::contains);
}