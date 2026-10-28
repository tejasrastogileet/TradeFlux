package com.tradeflux.portfolio_service.controller;


import com.tradeflux.portfolio_service.dtos.PortfolioPageResponse;
import com.tradeflux.portfolio_service.dtos.PortfolioSummaryResponse;
import com.tradeflux.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;


    @GetMapping("/my")
    public PortfolioPageResponse getMyHoldings(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size
    ) {
        log.info(
                "Request received to fetch portfolio holdings. Page: {}, Size: {}",
                page,
                size
        );

        return portfolioService.getMyHoldings(
                page,
                size
        );
    }


    @GetMapping("/summary")
    public PortfolioSummaryResponse getPortfolioSummary() {

        log.info(
                "Request received to fetch portfolio summary"
        );

        return portfolioService.getPortfolioSummary();
    }
}