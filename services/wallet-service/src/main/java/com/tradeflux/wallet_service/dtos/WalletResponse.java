package com.tradeflux.wallet_service.dtos;

import com.tradeflux.wallet_service.enums.Currency;
import lombok.*;

import java.math.BigDecimal;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {

    private Long walletId;

    private Long userId;

    private BigDecimal balance;
    private BigDecimal reservedBalance;
    private Currency currency;
}
