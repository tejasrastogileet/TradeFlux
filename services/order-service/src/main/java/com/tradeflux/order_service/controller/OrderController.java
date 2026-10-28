package com.tradeflux.order_service.controller;


import com.tradeflux.events.enums.OrderStatus;
import com.tradeflux.order_service.auth.RoleAllowed;
import com.tradeflux.order_service.dtos.*;
import com.tradeflux.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody
                                                    PlaceOrderRequest request){

        return new ResponseEntity<>(orderService.placeOrder(request),
                HttpStatus.CREATED);
    }

    @GetMapping("/my")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<Page<OrderSummaryResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(orderService.getMyOrders(
                page, size
        ));
    }

    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<Page<OrderSummaryResponse>> getMyOrdersByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(orderService.getMyOrdersByStatus(status,
                page,size));
    }

    @GetMapping("/{orderId}")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId){
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }


    @GetMapping("/search")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<Page<OrderSummaryResponse>> searchOrders(@RequestParam String keyword,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(orderService.searchOrders(keyword, page, size));
    }

    @PutMapping("/{orderId}/cancel")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<CancelOrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));

    }

    @GetMapping("/{orderId}/status")
    @RoleAllowed({"USER", "ADMIN"})
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable Long orderId ) {
        return ResponseEntity.ok(orderService.getOrderStatus(orderId));
    }

    @PostMapping("/{orderId}/execute")
    public ResponseEntity<ExecuteOrderResponse> executeOrder(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(orderService.executeOrder(orderId));
    }
}
