package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.event.dto.ChatReadEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatReadConsumer {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
        topics = "chat-read",
        groupId = "chat-read-group"
    )
    public void consume(ChatReadEvent event) {
        log.info("읽음 이벤트 수신: {}", event);

        messagingTemplate.convertAndSend("/topic/chat/read/" + event.coupleId(), event);
    }
}
