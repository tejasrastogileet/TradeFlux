package com.tradeflux.wallet_service.entity;


import com.tradeflux.wallet_service.enums.Currency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_wallet_user_id",
                        columnNames = "user_id"
                )
        },
        indexes = {

                @Index(
                        name = "idx_wallet_user_id",
                        columnList = "user_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    // Reserved amount for future
    // Order execution workflows
    @Column(name = "reserved_balance",nullable =false, precision = 19, scale = 4)
    private BigDecimal reservedBalance;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 10
    )
    private Currency currency;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (balance == null) {
            balance = BigDecimal.ZERO;
        }

        if (reservedBalance == null) {
            reservedBalance = BigDecimal.ZERO;
        }

        if (currency == null) {
            currency = Currency.INR;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
