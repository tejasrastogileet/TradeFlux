package com.tradeflux.portfolio_service.service.Impl;


import com.tradeflux.portfolio_service.auth.AuthenticatedUser;
import com.tradeflux.portfolio_service.auth.UserContextHolder;
import com.tradeflux.portfolio_service.dtos.HoldingResponse;
import com.tradeflux.portfolio_service.dtos.PortfolioPageResponse;
import com.tradeflux.portfolio_service.dtos.PortfolioSummaryResponse;
import com.tradeflux.portfolio_service.entity.PortfolioHolding;
import com.tradeflux.portfolio_service.exceptions.ForbiddenException;
import com.tradeflux.portfolio_service.repository.PortfolioHoldingRepository;
import com.tradeflux.portfolio_service.service.PortfolioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioHoldingRepository portfolioHoldingRepository;
    private final ModelMapper modelMapper;


    @Override
    @Transactional(readOnly = true)
    public PortfolioPageResponse getMyHoldings(int page, int size) {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Fetching portfolio holdings for userId: {}, page: {}, size: {}",
                currentUser.getUserId(), page, size);

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("symbol").ascending());

        Page<PortfolioHolding> holdings = portfolioHoldingRepository.findByUserId(
                currentUser.getUserId(), pageable
        );

        List<HoldingResponse> holdingResponses = holdings.getContent()
                .stream()
                .map(holding->
                        modelMapper.map(holding, HoldingResponse.class))
                .toList();

        return PortfolioPageResponse.builder()
                .holdings(holdingResponses)
                .currentPage(holdings.getNumber())
                .totalPages(holdings.getTotalPages())
                .totalElements(holdings.getTotalElements())
                .last(holdings.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioSummaryResponse getPortfolioSummary() {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Fetching portfolio Summary fot userId: {}",currentUser.getUserId());

        List<PortfolioHolding> holdings = portfolioHoldingRepository.findAllByUserId(
                currentUser.getUserId()
        );
        Integer totalHoldings = holdings.size();
        Integer totalQuantity =holdings.stream()
                .mapToInt(PortfolioHolding::getQuantity).sum();

        BigDecimal totalInvestedAmount =
                holdings.stream()
                        .map(
                                PortfolioHolding::getTotalInvestedAmount
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return PortfolioSummaryResponse.builder()
                .totalHoldings(totalHoldings)
                .totalQuantity(totalQuantity)
                .totalInvestedAmount(totalInvestedAmount)
                .build();
    }

    private AuthenticatedUser getAuthenticatedUser() {

        AuthenticatedUser authenticatedUser =
                UserContextHolder
                        .getCurrentUser();

        if (authenticatedUser == null) {

            throw new ForbiddenException(
                    "Access denied"
            );
        }
        return authenticatedUser;
    }
}
