package com.tradeflux.events;


import com.tradeflux.events.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledEvent {

    private Long orderId;
    private Long userId;
    private String symbol;
    private OrderStatus status;
    private LocalDateTime cancelledAt;
}