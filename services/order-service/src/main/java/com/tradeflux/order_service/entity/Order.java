package com.tradeflux.order_service.entity;


import com.tradeflux.events.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(
                        name = "idx_order_user_id",
                        columnList = "user_id"
                ),

                @Index(
                        name = "idx_order_status",
                        columnList = "status"
                ),

                @Index(
                        name = "idx_order_symbol",
                        columnList = "symbol"
                ),
                @Index(
                        name = "idx_order_created_at",
                        columnList = "created_at"
                ),
                @Index(
                        name = "idx_order_user_status",
                        columnList = "user_id,status"
                ),
                @Index(
                        name = "idx_order_user_created_at",
                        columnList = "user_id,created_at"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id",nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderSide orderSide;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private Integer quantity;

    @Column(
            precision = 19,
            scale = 4,
            nullable = false
    )
    private BigDecimal price;

    @Column(
            precision = 19,
            scale = 4,
            nullable = false
    )
    private BigDecimal totalAmount;

    private LocalDateTime executedAt;
    private LocalDateTime cancelledAt;

    @Column(length = 1000)
    private String failureReason;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
