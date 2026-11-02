package com.tradeflux.portfolio_service.service;

import com.tradeflux.portfolio_service.dtos.PortfolioPageResponse;
import com.tradeflux.portfolio_service.dtos.PortfolioSummaryResponse;

public interface PortfolioService {


    /**
     * Fetch authenticated user's holdings
     * with pagination.
     */

    PortfolioPageResponse getMyHoldings(int page, int size);


    /**
     * Fetch portfolio summary
     * of authenticated user.
     */
    PortfolioSummaryResponse getPortfolioSummary();
}
