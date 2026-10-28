package com.tradeflux.wallet_service.dtos;

import com.tradeflux.wallet_service.enums.TransactionSource;
import com.tradeflux.wallet_service.enums.TransactionStatus;
import com.tradeflux.wallet_service.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionStatus status;
    private TransactionSource source;
    private String referenceId;
    private String description;
    private LocalDateTime createdAt;
}