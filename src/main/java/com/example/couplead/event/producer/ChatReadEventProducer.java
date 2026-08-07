package com.example.couplead.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.event.dto.ChatReadEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatReadEventProducer {
    private final KafkaTemplate<String, ChatReadEvent> kfakaTemplate;

    public void publish(ChatReadEvent event) {
        kfakaTemplate.send(
            "chat-read",
            event.coupleId().toString(),
            event
        );
    }
}
