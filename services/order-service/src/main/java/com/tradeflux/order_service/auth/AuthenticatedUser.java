package com.tradeflux.order_service.auth;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticatedUser {

    private Long userId;
    private String email;
    private String role;
}
