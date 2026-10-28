package com.tradeflux.order_service.clients.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsumeFundsResponse {


    private Long userId;
    private BigDecimal consumedAmount;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private String message;
}
