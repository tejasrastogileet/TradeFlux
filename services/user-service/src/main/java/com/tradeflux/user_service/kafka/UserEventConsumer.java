package com.tradeflux.user_service.kafka;


import com.tradeflux.events.UserRegisteredEvent;
import com.tradeflux.user_service.entity.UserProfile;
import com.tradeflux.user_service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserProfileRepository userProfileRepository;

    @KafkaListener(
            topics = "user-registered",
            groupId = "user-service-group",
            containerFactory =
                    "userRegisteredKafkaListenerFactory"
    )
    @Transactional
    public void consumeUserRegisteredEvent(
            UserRegisteredEvent event,
            Acknowledgment acknowledgment
    ) {

        log.info(
                "Received UserRegisteredEvent for user: {}",
                event.email()
        );
        if (
                userProfileRepository.existsByAuthUserId(
                        event.userId()
                )
        ) {
            log.warn(
                    "User profile already exists for authUserId: {}",
                    event.userId()
            );
            acknowledgment.acknowledge();
            return;
        }
        UserProfile profile = UserProfile.builder()
                .authUserId(event.userId())
                .email(event.email())
                .username(
                        generateUsername(event.email())
                )
                .build();

        userProfileRepository.save(profile);
        log.info(
                "User profile created successfully for user: {}",
                event.email()
        );
        acknowledgment.acknowledge();
    }


    private String generateUsername(
            String email
    ) {

        return email.split("@")[0];
    }
}