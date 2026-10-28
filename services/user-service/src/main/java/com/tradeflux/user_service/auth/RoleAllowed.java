package com.tradeflux.user_service.auth;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RoleAllowed {

    String[] value();
}