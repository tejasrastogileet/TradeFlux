package com.tradeflux.order_service.clients.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseFundsRequest {

    private Long userId;

    private BigDecimal amount;
}
