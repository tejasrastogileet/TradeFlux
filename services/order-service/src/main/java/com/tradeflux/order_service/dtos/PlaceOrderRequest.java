package com.tradeflux.order_service.dtos;

import com.tradeflux.events.enums.AssetType;
import com.tradeflux.events.enums.OrderSide;
import com.tradeflux.events.enums.OrderType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderRequest {

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotNull(message = "Asset type is required")
    private AssetType assetType;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotNull(message = "Order side is required")
    private OrderSide orderSide;

    @NotNull(message = "Quantity is required")
    @Min(
            value = 1,
            message = "Quantity must be greater than 0"
    )
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than 0"
    )
    private BigDecimal price;
}