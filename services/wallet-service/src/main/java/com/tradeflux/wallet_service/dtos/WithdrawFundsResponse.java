package com.tradeflux.wallet_service.dtos;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawFundsResponse {

    private Long transactionId;
    private BigDecimal withdrawnAmount;
    private BigDecimal updatedBalance;
    private String message;
}
