package com.tradeflux.portfolio_service.kafka;

import com.tradeflux.events.OrderExecutedEvent;
import com.tradeflux.events.enums.OrderSide;
import com.tradeflux.portfolio_service.entity.PortfolioHolding;
import com.tradeflux.portfolio_service.exceptions.BadRequestException;
import com.tradeflux.portfolio_service.repository.PortfolioHoldingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExecutedEventConsumer {

    private final PortfolioHoldingRepository portfolioHoldingRepository;

    @KafkaListener(
            topics = "order-executed-topic",
            groupId = "portfolio-group"
    )
    @Transactional
    public void consume(
            OrderExecutedEvent event
    ) {

        log.info(
                "Received OrderExecutedEvent. OrderId: {}, UserId: {}, Symbol: {}",
                event.getOrderId(),
                event.getUserId(),
                event.getSymbol()
        );

        Optional<PortfolioHolding> existingHolding =
                portfolioHoldingRepository.findByUserIdAndSymbol(
                        event.getUserId(),
                        event.getSymbol()
                );

        if (existingHolding.isPresent()) {

            updateHolding(
                    existingHolding.get(),
                    event
            );

        } else {

            createHolding(event);
        }

        log.info(
                "Portfolio updated successfully. UserId: {}, Symbol: {}",
                event.getUserId(),
                event.getSymbol()
        );
    }

    private void createHolding(
            OrderExecutedEvent event
    ) {

        if (event.getOrderSide() == OrderSide.SELL) {

            throw new BadRequestException(
                    "Cannot sell asset that is not owned"
            );
        }

        PortfolioHolding holding =
                PortfolioHolding.builder()
                        .userId(event.getUserId())
                        .symbol(event.getSymbol())
                        .quantity(event.getQuantity())
                        .averageBuyPrice(event.getPrice())
                        .totalInvestedAmount(event.getTotalAmount())
                        .lastTransactionAt(LocalDateTime.now())
                        .build();

        portfolioHoldingRepository.save(holding);

        log.info(
                "Created new holding. UserId: {}, Symbol: {}",
                event.getUserId(),
                event.getSymbol()
        );
    }

    private void updateHolding(
            PortfolioHolding holding,
            OrderExecutedEvent event
    ) {

        if (event.getOrderSide() == OrderSide.BUY) {

            processBuy(
                    holding,
                    event
            );

        } else {

            processSell(
                    holding,
                    event
            );
        }
    }

    private void processBuy(
            PortfolioHolding holding,
            OrderExecutedEvent event
    ) {

        Integer oldQuantity =
                holding.getQuantity();

        BigDecimal oldInvestment =
                holding.getTotalInvestedAmount();

        Integer newQuantity =
                oldQuantity + event.getQuantity();

        BigDecimal newInvestment =
                oldInvestment.add(
                        event.getTotalAmount()
                );

        BigDecimal averagePrice =
                newInvestment.divide(
                        BigDecimal.valueOf(newQuantity),
                        4,
                        RoundingMode.HALF_UP
                );

        holding.setQuantity(
                newQuantity
        );

        holding.setTotalInvestedAmount(
                newInvestment
        );

        holding.setAverageBuyPrice(
                averagePrice
        );

        holding.setLastTransactionAt(
                LocalDateTime.now()
        );

        portfolioHoldingRepository.save(
                holding
        );

        log.info(
                "BUY executed. UserId: {}, Symbol: {}, Quantity: {}",
                holding.getUserId(),
                holding.getSymbol(),
                newQuantity
        );
    }

    private void processSell(
            PortfolioHolding holding,
            OrderExecutedEvent event
    ) {

        if (holding.getQuantity() < event.getQuantity()) {

            throw new BadRequestException(
                    "Insufficient quantity available to sell"
            );
        }

        int remainingQuantity =
                holding.getQuantity()
                        - event.getQuantity();

        if (remainingQuantity == 0) {

            portfolioHoldingRepository.delete(
                    holding
            );

            log.info(
                    "Holding removed. UserId: {}, Symbol: {}",
                    holding.getUserId(),
                    holding.getSymbol()
            );

            return;
        }

        holding.setQuantity(
                remainingQuantity
        );

        holding.setLastTransactionAt(
                LocalDateTime.now()
        );

        portfolioHoldingRepository.save(
                holding
        );

        log.info(
                "SELL executed. UserId: {}, Symbol: {}, Remaining Quantity: {}",
                holding.getUserId(),
                holding.getSymbol(),
                remainingQuantity
        );
    }
}