package com.tradeflux.auth_service.kafka;

import com.tradeflux.events.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_REGISTERED_TOPIC =
            "user-registered";

    public void publishUserRegisteredEvent(
            UserRegisteredEvent event
    ) {

        log.info(
                "Publishing UserRegisteredEvent for user: {}",
                event.email()
        );

        kafkaTemplate.send(
                USER_REGISTERED_TOPIC,
                event
        );
    }
}