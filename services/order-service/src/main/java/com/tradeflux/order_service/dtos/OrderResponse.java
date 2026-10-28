package com.tradeflux.order_service.dtos;


import com.tradeflux.events.enums.AssetType;
import com.tradeflux.events.enums.OrderSide;
import com.tradeflux.events.enums.OrderStatus;
import com.tradeflux.events.enums.OrderType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private String symbol;
    private AssetType assetType;
    private OrderType orderType;
    private OrderSide orderSide;
    private OrderStatus status;

    private Integer quantity;

    private BigDecimal price;
    private LocalDateTime executedAt;
    private LocalDateTime cancelledAt;

    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
