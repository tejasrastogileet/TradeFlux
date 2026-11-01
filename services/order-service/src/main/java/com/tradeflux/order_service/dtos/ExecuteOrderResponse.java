package com.tradeflux.order_service.dtos;


import com.tradeflux.events.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExecuteOrderResponse {

    private Long orderId;
    private OrderStatus status;
    private String message;
}
