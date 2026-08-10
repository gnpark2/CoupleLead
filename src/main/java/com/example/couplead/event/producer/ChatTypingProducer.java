package com.example.couplead.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.typing.dto.ChatTypingEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatTypingProducer {
    private final KafkaTemplate<String, ChatTypingEvent> kafkaTemplate;

    public void publish(ChatTypingEvent event) {
        kafkaTemplate.send(
            "chat-typing",
            event.coupleId().toString(),
            event
        );
    }
}
