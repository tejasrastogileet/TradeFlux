package com.tradeflux.order_service.repository;

import com.tradeflux.events.enums.OrderStatus;
import com.tradeflux.order_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order,Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Long userId,
                                      OrderStatus status,
                                      Pageable pageable);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

    Page<Order> findByUserIdAndSymbolContainingIgnoreCase(
            Long userId,
            String symbol,
            Pageable pageable
    );

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    boolean existsByIdAndUserId(Long orderId, Long userId);
}
