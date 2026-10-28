package com.tradeflux.wallet_service.dtos;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseFundsResponse {

    private Long userId;
    private BigDecimal releasedAmount;
    private BigDecimal availableBalance;
    private BigDecimal reservedBalance;
    private String message;
}
