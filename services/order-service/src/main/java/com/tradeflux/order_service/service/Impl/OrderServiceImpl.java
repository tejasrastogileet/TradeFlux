package com.tradeflux.order_service.service.Impl;

import com.tradeflux.events.OrderCancelledEvent;
import com.tradeflux.events.OrderExecutedEvent;
import com.tradeflux.events.OrderPlacedEvent;
import com.tradeflux.order_service.auth.AuthenticatedUser;
import com.tradeflux.order_service.auth.UserContextHolder;
import com.tradeflux.order_service.clients.WalletClient;
import com.tradeflux.order_service.clients.dto.ConsumeFundsRequest;
import com.tradeflux.order_service.clients.dto.ReleaseFundsRequest;
import com.tradeflux.order_service.clients.dto.ReserveFundsRequest;
import com.tradeflux.order_service.dtos.*;
import com.tradeflux.order_service.entity.Order;
import com.tradeflux.events.enums.*;
import com.tradeflux.order_service.exceptions.BadRequestException;
import com.tradeflux.order_service.exceptions.ForbiddenException;
import com.tradeflux.order_service.exceptions.ResourceNotFoundException;
import com.tradeflux.order_service.kafka.OrderEventProducer;
import com.tradeflux.order_service.repository.OrderRepository;
import com.tradeflux.order_service.service.OrderService;
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
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final ModelMapper modelMapper;
    private final WalletClient walletClient;

    @Override
    public OrderResponse placeOrder(PlaceOrderRequest request) {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Placing order for userId; {}, symbol: {}",currentUser.getUserId(),
                request.getSymbol());

        validateOrderRequest(request);

        BigDecimal totalAmount = request.getPrice()
                .multiply(
                        BigDecimal.valueOf(
                                request.getQuantity()
                        )
                );

        walletClient.reserveFunds(
                ReserveFundsRequest.builder()
                        .userId(currentUser.getUserId())
                        .amount(totalAmount)
                        .build()
        );

        log.info(
                "Funds reserved successfully for userId: {}, amount: {}",
                currentUser.getUserId(),
                totalAmount
        );

         Order order = Order.builder()
                 .userId(currentUser.getUserId())
                 .symbol(request.getSymbol().toUpperCase())
                 .assetType(request.getAssetType())
                 .orderType(request.getOrderType())
                 .orderSide(request.getOrderSide())
                 .quantity(request.getQuantity())
                 .price(request.getPrice())
                 .totalAmount(totalAmount)
                 .status(OrderStatus.PENDING)
                 .build();

         Order savedOrder  = orderRepository.save(order);
         log.info("Order created successfully: orderId: {}",savedOrder.getId());

        OrderPlacedEvent event =
                OrderPlacedEvent.builder()
                        .orderId(savedOrder.getId())
                        .userId(savedOrder.getUserId())
                        .symbol(savedOrder.getSymbol())
                        .assetType(savedOrder.getAssetType())
                        .orderType(savedOrder.getOrderType())
                        .orderSide(savedOrder.getOrderSide())
                        .status(savedOrder.getStatus())
                        .quantity(savedOrder.getQuantity())
                        .price(savedOrder.getPrice())
                        .totalAmount(savedOrder.getTotalAmount())
                        .createdAt(savedOrder.getCreatedAt())
                        .build();

        orderEventProducer
                .publishOrderPlacedEvent(event);

         return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getMyOrders(int page, int size) {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Fetching orders for userId: {}",currentUser.getUserId());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt")
                .ascending());

        Page<Order> orders = orderRepository.findByUserId(
                currentUser.getUserId(),  pageable
        );
        return orders.map(order->
                modelMapper.map(order, OrderSummaryResponse.class));
    }

    @Override
    public Page<OrderSummaryResponse> getMyOrdersByStatus(OrderStatus status, int page, int size) {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Fetching {} order for userId: {}",status, currentUser.getUserId());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt")
                .descending());

        Page<Order> orders = orderRepository.findByUserIdAndStatus(currentUser.getUserId(),
                status, pageable);

        return orders.map(order->modelMapper.map(
                order, OrderSummaryResponse.class
        ));
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        AuthenticatedUser currentUser = getAuthenticatedUser();
        log.info("Fetching orderId: {} for userId: {}",orderId,
                currentUser.getUserId());

        log.info("Fetching orderId: {} for userId: {}",orderId, currentUser.getUserId());
        Order order = orderRepository.findByIdAndUserId(orderId, currentUser.getUserId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Order not found"));

        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public Page<OrderSummaryResponse> searchOrders(String keyword, int page, int size) {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Searching orders for userId: {}, keyword: {}",
                currentUser.getUserId(),
                keyword);

        Pageable pageable = PageRequest.of(page,size,
                Sort.by("createdAt")
                        .descending()
        );

        Page<Order> orders = orderRepository.findByUserIdAndSymbolContainingIgnoreCase(
                currentUser.getUserId(), keyword, pageable
        );

        return orders.map(order->modelMapper.map(
                order, OrderSummaryResponse.class
        ));
    }

    @Override
    public CancelOrderResponse cancelOrder(Long orderId) {

        AuthenticatedUser currentUser = getAuthenticatedUser();

        log.info("Cancelling orderId: {} for userId: {}", orderId, currentUser.getUserId());


        Order order = orderRepository.findByIdAndUserId(orderId,
                currentUser.getUserId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Order not found"));

        if(order.getStatus()==OrderStatus.CANCELLED){
            throw new BadRequestException("Order already cancelled");
        }

        if(order.getStatus()==OrderStatus.EXECUTED){
            throw new ForbiddenException("Executed order cannot be cancelled");
        }


        walletClient.releaseFunds(
                ReleaseFundsRequest.builder()
                        .userId(order.getUserId())
                        .amount(order.getTotalAmount())
                        .build()
        );

        log.info(
                "Funds released successfully for orderId: {}",
                order.getId()
        );

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(
                LocalDateTime.now()
        );
        orderRepository.save(order);

        log.info("Order cancelled successfully: orderId: {}",orderId);

        OrderCancelledEvent event =
                OrderCancelledEvent.builder()
                        .orderId(order.getId())
                        .userId(order.getUserId())
                        .symbol(order.getSymbol())
                        .status(order.getStatus())
                        .cancelledAt(order.getCancelledAt())
                        .build();

        orderEventProducer
                .publishOrderCancelledEvent(event);

        return CancelOrderResponse.builder().
                orderId(order.getId())
                        .status(order.getStatus())
                .message("Order cancelled successfully")
                .build();

    }

    @Override
    public OrderStatusResponse getOrderStatus(Long orderId) {

        AuthenticatedUser currentUser = getAuthenticatedUser();
        Order order = orderRepository.findByIdAndUserId(
                        orderId,
                        currentUser.getUserId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found"
                        )
                );

        return OrderStatusResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .message(
                        "Order status fetched successfully"
                )
                .build();
    }

    @Override
    @Transactional
    public ExecuteOrderResponse executeOrder(Long orderId) {


        AuthenticatedUser currentUser = getAuthenticatedUser();

        Order order = orderRepository.findByIdAndUserId(orderId, currentUser.getUserId())
                .orElseThrow(()->
                        new ResourceNotFoundException("Order not found"));

        if(order.getStatus() != OrderStatus.PENDING){
            throw new BadRequestException("Only pending orders can be executed");
        }

        walletClient.consumeFunds(
                ConsumeFundsRequest.builder()
                        .userId(order.getUserId())
                        .amount(order.getTotalAmount())
                        .build()
        );

        log.info(
                "Funds consumed successfully for orderId: {}",
                order.getId()
        );

        order.setStatus(OrderStatus.EXECUTED);
        order.setExecutedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        OrderExecutedEvent event =
                OrderExecutedEvent.builder()
                        .orderId(savedOrder.getId())
                        .userId(savedOrder.getUserId())
                        .symbol(savedOrder.getSymbol())
                        .assetType(savedOrder.getAssetType())
                        .orderSide(savedOrder.getOrderSide())
                        .quantity(savedOrder.getQuantity())
                        .price(savedOrder.getPrice())
                        .totalAmount(savedOrder.getTotalAmount())
                        .executedAt(savedOrder.getExecutedAt())
                        .build();

        orderEventProducer
                .publishOrderExecutedEvent(event);

        return ExecuteOrderResponse.builder()
                .orderId(savedOrder.getId())
                .status(savedOrder.getStatus())
                .message("Order executed successfully")
                .build();
    }


    private void validateOrderRequest(
            PlaceOrderRequest request
    ) {
        if (request.getQuantity() <= 0) {

            throw new BadRequestException(
                    "Quantity must be greater than 0"
            );
        }
        if (request.getPrice()
                .compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "Price must be greater than 0"
            );
        }
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
