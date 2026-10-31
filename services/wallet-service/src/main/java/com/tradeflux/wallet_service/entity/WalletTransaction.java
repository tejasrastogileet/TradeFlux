package com.tradeflux.wallet_service.entity;


import com.tradeflux.wallet_service.enums.TransactionSource;
import com.tradeflux.wallet_service.enums.TransactionStatus;
import com.tradeflux.wallet_service.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "wallet_transactions",
        indexes = {

                @Index(
                        name = "idx_wallet_txn_user_id",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_wallet_txn_wallet_id",
                        columnList = "wallet_id"
                ),

                @Index(
                        name = "idx_wallet_txn_created_at",
                        columnList = "created_at"
                ),

                @Index(
                        name = "idx_wallet_txn_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_wallet_txn_user_created",
                        columnList = "user_id,created_at"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id",nullable = false)
    private Wallet wallet;

    @Column(nullable = false,precision =  19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TransactionSource source;

    @Column(length = 100)
    private String referenceId;

    @Column(length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
