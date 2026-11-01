package com.tradeflux.order_service.kafka;


import com.tradeflux.events.OrderCancelledEvent;
import com.tradeflux.events.OrderExecutedEvent;
import com.tradeflux.events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderPlacedEvent(OrderPlacedEvent event){

        log.info("Publishing OrderPlacedEvent for orderId: {}",event.getOrderId());

        kafkaTemplate.send(
                KafkaTopics.ORDER_PLACED,
                event.getOrderId().toString(),
                event
        );
    }

    public void publishOrderCancelledEvent(
            OrderCancelledEvent event
    ) {
        log.info(
                "Publishing OrderCancelledEvent for orderId: {}",
                event.getOrderId()
        );

        kafkaTemplate.send(
                KafkaTopics.ORDER_CANCELLED,
                event.getOrderId().toString(),
                event
        );
    }


    public void publishOrderExecutedEvent(
            OrderExecutedEvent event
    ) {

        kafkaTemplate.send(
                KafkaTopics.ORDER_EXECUTED,
                event.getOrderId().toString(),
                event
        );

        log.info(
                "Published OrderExecutedEvent for orderId: {}",
                event.getOrderId()
        );
    }
}
