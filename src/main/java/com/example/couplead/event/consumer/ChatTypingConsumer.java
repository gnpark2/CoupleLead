package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.typing.dto.ChatTypingEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatTypingConsumer {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
        topics = "chat-typing",
        groupId = "chat-typing-group"
    )
    public void consume(ChatTypingEvent event) {
        log.info("Typing event: {}", event);
        messagingTemplate.convertAndSend("/topic/chat/typing/" + event.coupleId(), event);
    }
}
