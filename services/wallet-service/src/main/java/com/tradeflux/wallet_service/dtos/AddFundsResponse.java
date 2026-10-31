package com.tradeflux.wallet_service.dtos;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddFundsResponse {

    private Long transactionId;
    private BigDecimal amountAdded;
    private BigDecimal updatedBalance;
    private String message;
}
