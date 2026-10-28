package com.tradeflux.order_service.dtos;

import com.tradeflux.events.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelOrderResponse {

    private Long orderId;
    private OrderStatus status;
    private String message;
}
