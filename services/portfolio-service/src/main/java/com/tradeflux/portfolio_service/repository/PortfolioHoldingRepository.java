package com.tradeflux.portfolio_service.repository;

import com.tradeflux.portfolio_service.entity.PortfolioHolding;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioHoldingRepository extends JpaRepository<PortfolioHolding, Long> {

    Optional<PortfolioHolding> findByUserIdAndSymbol(Long userId, String symbol);

    Page<PortfolioHolding> findByUserId(Long userId, Pageable pageable);

    List<PortfolioHolding> findAllByUserId(Long userId);


    // search holding
    Page<PortfolioHolding> findByUserIdAndSymbolContainingIgnoreCase(
            Long userId, String symbol, Pageable pageable
    );


    // ADMIN/ANALYTIC
    List<PortfolioHolding> findBySymbol(String symbol);

    boolean existsByUserIdAndSymbol(Long userId,
                                    String symbol);
}
