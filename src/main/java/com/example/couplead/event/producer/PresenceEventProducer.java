package com.example.couplead.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.presence.dto.UserPresenceEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PresenceEventProducer {
    private final KafkaTemplate<String, UserPresenceEvent> kafkaTemplate;

    public void publish(UserPresenceEvent event) {
        kafkaTemplate.send(
            "user-presence",
            event.userId().toString(),
            event
        );
    }
}
