package com.tradeflux.events;

import java.time.LocalDateTime;

public record UserRegisteredEvent (


        Long userId,
        String email,
        LocalDateTime registeredAt
){
}
