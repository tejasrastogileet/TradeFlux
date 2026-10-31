package com.tradeflux.wallet_service.dtos;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumeFundsResponse {

    private Long userId;
    private BigDecimal consumedAmount;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private String message;
}