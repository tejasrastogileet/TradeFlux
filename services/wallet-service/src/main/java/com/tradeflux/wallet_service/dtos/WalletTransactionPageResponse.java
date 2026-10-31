package com.tradeflux.wallet_service.dtos;


import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionPageResponse {

    private List<TransactionResponse> transactions;
    private Integer currentPage;
    private Integer totalPages;
    private Long totalElements;
    private Boolean last;
}