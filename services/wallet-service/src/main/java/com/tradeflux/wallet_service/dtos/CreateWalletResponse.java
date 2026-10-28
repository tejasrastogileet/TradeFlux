package com.tradeflux.wallet_service.dtos;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletResponse {

    private Long walletId;
    private Long userId;

    private String message;
}
