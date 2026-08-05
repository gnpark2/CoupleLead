package com.example.couplead.event.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.chat.dto.response.ChatMessageResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatEventProducer {
    private final KafkaTemplate<String, ChatMessageResponse> kafkaTemplate;
    
    public void publish(ChatMessageResponse message) {
        kafkaTemplate.send(
            "chat-message",
            message.coupleId().toString(),
            message
        );
    }
}
