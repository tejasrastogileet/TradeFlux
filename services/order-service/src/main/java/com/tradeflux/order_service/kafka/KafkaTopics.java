package com.tradeflux.order_service.kafka;

public final class KafkaTopics {

    private KafkaTopics() {
    }

    public static final String ORDER_PLACED =
            "order-placed-topic";

    public static final String ORDER_CANCELLED =
            "order-cancelled-topic";

    public static final String ORDER_EXECUTED =
            "order-executed-topic";
}