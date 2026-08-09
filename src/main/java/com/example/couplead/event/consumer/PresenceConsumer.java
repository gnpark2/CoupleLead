package com.example.couplead.event.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.couplead.presence.dto.UserPresenceEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresenceConsumer {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
        topics = "user-presence",
        groupId = "presence-group"
    )

    public void consume(UserPresenceEvent event) {
        log.info("온라인 상태 이벤트: {}", event);

        messagingTemplate.convertAndSend("/topic/presnce" + event.userId(), event);
    }
}
