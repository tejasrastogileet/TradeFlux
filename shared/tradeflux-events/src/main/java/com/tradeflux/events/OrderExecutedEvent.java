package com.tradeflux.events;


import com.tradeflux.events.enums.AssetType;
import com.tradeflux.events.enums.OrderSide;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderExecutedEvent {

    private Long orderId;
    private Long userId;

    private String symbol;

    private AssetType assetType;
    private OrderSide orderSide;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal  totalAmount;

    private LocalDateTime executedAt;
}
