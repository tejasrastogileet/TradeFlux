package com.tradeflux.order_service.service;

import com.tradeflux.events.enums.*;
import com.tradeflux.order_service.dtos.*;

import org.springframework.data.domain.Page;

public interface OrderService {

    OrderResponse placeOrder(PlaceOrderRequest request);

    Page<OrderSummaryResponse> getMyOrders(int page, int size);

    Page<OrderSummaryResponse> getMyOrdersByStatus(OrderStatus status,
                                                   int page, int size);

    OrderResponse getOrderById(Long orderId);

    Page<OrderSummaryResponse> searchOrders(String keyword, int page, int size);

    CancelOrderResponse cancelOrder(Long orderId);

    OrderStatusResponse getOrderStatus(Long orderId);


    ExecuteOrderResponse executeOrder(Long orderId);
}
