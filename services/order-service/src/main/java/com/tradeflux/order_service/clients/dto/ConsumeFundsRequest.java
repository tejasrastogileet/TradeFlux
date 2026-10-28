package com.tradeflux.order_service.clients.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsumeFundsRequest {

    private Long userId;
    private BigDecimal amount;
}
