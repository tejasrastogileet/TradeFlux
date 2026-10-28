package com.tradeflux.portfolio_service.dtos;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryResponse {

    private Integer totalHoldings;
    private Integer totalQuantity;
    private BigDecimal totalInvestedAmount;
}