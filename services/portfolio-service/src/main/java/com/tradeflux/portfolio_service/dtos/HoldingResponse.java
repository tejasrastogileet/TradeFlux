package com.tradeflux.portfolio_service.dtos;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldingResponse {

    private Long id;
    private String symbol;
    private Integer quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal totalInvestedAmount;
    private LocalDateTime lastTransactionAt;
}