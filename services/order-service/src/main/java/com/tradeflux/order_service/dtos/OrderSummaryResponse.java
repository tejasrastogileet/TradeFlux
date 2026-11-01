package com.tradeflux.order_service.dtos;



import com.tradeflux.events.enums.OrderSide;
import com.tradeflux.events.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {

    private Long id;
    private String symbol;
    private OrderSide orderSide;
    private OrderStatus status;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}