package com.tradeflux.portfolio_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "portfolio_holdings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_symbol",
                        columnNames = {
                                "user_id",
                                "symbol"
                        }
                )
        },
        indexes = {

                @Index(
                        name = "idx_portfolio_user_id",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_portfolio_symbol",
                        columnList = "symbol"
                ),

                @Index(
                        name = "idx_portfolio_user_symbol",
                        columnList = "user_id,symbol"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(nullable = false, length = 30)
    private String symbol;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "average_buy_price",
            nullable = false,
            precision = 19,
            scale = 4)
    private BigDecimal averageBuyPrice;

    @Column(
            name = "total_invested_amount",
            nullable = false,
            precision = 19,
            scale = 4
    )
    private BigDecimal totalInvestedAmount;


    @Column(name = "last_transaction_at")
    private LocalDateTime lastTransactionAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.lastTransactionAt == null) {
            this.lastTransactionAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
